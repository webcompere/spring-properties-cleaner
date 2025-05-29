package uk.org.webcompere.spc.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.LineError;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.processor.writing.Writer;

public class Fixer {

    /**
     * Fix the files and write them out
     * @param propertiesFiles the files
     * @param config the configuration of the tool
     * @param writer the target writer
     * @return true unless it went wrong
     */
    public static boolean fix(List<PropertiesFile> propertiesFiles, SpcArgs config, Writer writer) {
        try {
            for (int i = 0; i < propertiesFiles.size(); i++) {
                fix(propertiesFiles.get(i), config);
            }

            writer.writeAll(
                    CommonSettings.process(propertiesFiles, config),
                    config.getSort(),
                    config.getWhiteSpaceMode(),
                    config.isYml());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Apply fixes to the file
     * @param file file to fix
     * @param config configuration
     */
    static void fix(PropertiesFile file, SpcArgs config) {
        file.getDuplicates().keySet().forEach(file::collapseIntoLast);

        List<LineError> errors = file.extractErrors();
        file.addTrailingComments(errors.stream()
                .map(error -> "# " + error.getLine() + ": " + error.getContent())
                .collect(Collectors.toList()));

        Sorting.applySort(file, config.getSort());

        if (config.getInlinePrefix() != null) {
            processInliningOfPrefixes(file, config.getInlinePrefix());
        }
    }

    private static void processInliningOfPrefixes(PropertiesFile file, String prefixRegex) {
        var items = file.getAsMap();
        Map<String, String> fixups = new HashMap<>();

        for (String propertyName : items.keySet()) {
            Inliner inliner = new Inliner(propertyName, prefixRegex);
            fixups.putAll(inliner.scanForInlining(items));
        }

        file.applyFixups(fixups);
    }
}
