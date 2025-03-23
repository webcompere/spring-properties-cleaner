package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static uk.org.webcompere.spc.parser.Lines.allTheSame;

public class CommonSettings {
    /**
     * Process the properties files to find and coalesce common elements. Can only operate when there's
     * more than one properties file present
     * @param propertiesFiles the files loaded
     * @param config the configuration
     * @return a new list of properties files containing the modified properties files, with a new one added for
     *   common properties if possible
     */
    public static List<PropertiesFile> process(List<PropertiesFile> propertiesFiles, SpcArgs config) {
        if (config.getCommonProperties().equals(SpcArgs.CommonPropertiesMode.none) || propertiesFiles.size() < 2) {
            return propertiesFiles;
        }

        // find or create the common file
        String commonFileName = config.getPrefix() + ".properties";
        PropertiesFile commonFile = propertiesFiles.stream()
                .filter(file -> file.getName().equals(commonFileName))
                .findFirst()
                .orElseGet(() -> new PropertiesFile(new File(propertiesFiles.get(0).getSource().getParentFile(), commonFileName)));

        // find the other files
        List<PropertiesFile> otherFiles = propertiesFiles.stream()
                .filter(file -> !file.getName().equals(commonFileName))
                .collect(toList());

        // shuffle properties around
        combineProperties(commonFile, otherFiles, config.getCommonProperties());

        // then put the files together
        return Stream.concat(Stream.of(commonFile), otherFiles.stream()).collect(toList());
    }

    private static void combineProperties(PropertiesFile commonFile, List<PropertiesFile> otherFiles,
                                          SpcArgs.CommonPropertiesMode mode) {
        PropertiesFile superProperties = new PropertiesFile(new File("super"));
        otherFiles.stream().map(PropertiesFile::getSettings).flatMap(List::stream).forEach(superProperties::add);

        var duplicates = superProperties.getDuplicates();

        duplicates.forEach((key, values) -> {
            combineProperties(commonFile, otherFiles, key, values, mode);
        });
    }

    private static void combineProperties(PropertiesFile commonFile, List<PropertiesFile> otherFiles,
                                          String key, List<Setting> sharedValues, SpcArgs.CommonPropertiesMode mode) {
        if (commonFile.getLast(key).isPresent()) {
            return;
        }

        // work out if eligible
        switch (mode) {
            case full:
                if (sharedValues.size() != otherFiles.size()) {
                    return;
                }
            case consistent:
                if (!allTheSame(sharedValues.stream().map(Setting::getValue))) {
                    return;
                }
                break;
            case multiple:
                applyDominant(commonFile, otherFiles, key, sharedValues);
                return;
            default:
                // we should already have filtered out `none`
                throw new IllegalArgumentException("This shouldn't be reachable");
        }

        commonFile.add(sharedValues.get(0));
        otherFiles.forEach(file -> file.remove(key));
    }

    private static void applyDominant(PropertiesFile commonFile, List<PropertiesFile> otherFiles,
                                      String key, List<Setting> sharedValues) {
        Map<String, Long> countOfValues = sharedValues.stream()
                .collect(groupingBy(Setting::getValue, counting()));

        Long max = countOfValues.values().stream()
                .mapToLong(val -> val)
                .max()
                .orElse(0);

        List<String> valuesAtMax = countOfValues.entrySet().stream()
                .filter(entry -> max.equals(entry.getValue()))
                .map(Map.Entry::getKey).collect(toList());

        // there is no winner
        if (valuesAtMax.size() != 1) {
            return;
        }

        var dominantValue = valuesAtMax.get(0);

        // select the first one with this value
        var toPromote = sharedValues.stream()
                .filter(value -> value.getValue().equals(dominantValue))
                .findFirst()
                .orElseThrow(); // should be unreachable unless maths is broken

        commonFile.add(toPromote);
        otherFiles.forEach(file -> file.removeIf(key, dominantValue));
    }
}
