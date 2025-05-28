package uk.org.webcompere.spc.processor;

import static uk.org.webcompere.spc.parser.Lines.streamPairs;

import java.util.List;
import java.util.stream.Collectors;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

public class Scanner {
    /**
     * Scan a properties file for dangerous or harmless duplicates and properties that telescope into each other
     * @param propertiesFile file
     * @return the results of the scan
     */
    public static ScanResult scanForIssues(PropertiesFile propertiesFile) {
        ScanResult scanResult = new ScanResult();

        var errorPrefix = propertiesFile.getName() + ": ";

        reportErrors(propertiesFile, scanResult, errorPrefix);
        reportDuplicates(propertiesFile, scanResult, errorPrefix);
        reportTelescopingProperties(propertiesFile, scanResult, errorPrefix);

        return scanResult;
    }

    private static void reportErrors(PropertiesFile propertiesFile, ScanResult scanResult, String errorPrefix) {
        propertiesFile
                .getLineErrors()
                .forEach(error -> scanResult.addError(
                        errorPrefix + "non property '" + error.getContent() + "' on L" + error.getLine()));
    }

    private static void reportDuplicates(PropertiesFile propertiesFile, ScanResult scanResult, String errorPrefix) {
        var duplicates = propertiesFile.getDuplicates();

        duplicates.forEach((key, value) -> {
            scanResult.getDuplicateKeys().add(key);
            scanResult.addError(errorPrefix + key + " has duplicate values "
                    + value.stream()
                            .map(val -> "L" + val.getLine() + ":'" + val.getValue() + "'")
                            .collect(Collectors.joining(",")));
        });
    }

    private static void reportTelescopingProperties(
            PropertiesFile propertiesFile, ScanResult scanResult, String errorPrefix) {
        List<String> keys = propertiesFile.getSettings().stream()
                .map(Setting::getFullPath)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        streamPairs(keys).forEach(pair -> {
            if (pair.getSecond().startsWith(pair.getFirst())) {
                scanResult.setTelescopingProperties(true);
                scanResult.addWarning(
                        errorPrefix + "property '" + pair.getFirst() + "' telescopes into '" + pair.getSecond() + "'");
            }
        });
    }
}
