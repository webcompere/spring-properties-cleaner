package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.util.stream.Collectors;

import static uk.org.webcompere.spc.parser.Lines.allTheSame;

public class Scanner {
    /**
     * Scan a properties file for dangerous or harmless duplicates
     * @param propertiesFile file
     * @return the results of the scan
     */
    public static ScanResult scanForIssues(PropertiesFile propertiesFile) {
        ScanResult scanResult = new ScanResult();

        var errorPrefix = propertiesFile.getName() + ": ";

        propertiesFile.getLineErrors().forEach(error ->
                scanResult.addError(errorPrefix + "non property '" + error.getContent() + "' on L" + error.getLine()));

        var duplicates = propertiesFile.getDuplicates();

        duplicates.forEach((key, value) -> {
            scanResult.getDuplicateKeys().add(key);
            if (allTheSame(value.stream().map(Setting::getValue))) {
                scanResult.addWarning(errorPrefix + key + " has duplicate value '" +
                        value.get(0).getValue() + "' on " +
                        value.stream().map(val -> "L" + val.getLine())
                                .collect(Collectors.joining(",")));
            } else {
                scanResult.addError(errorPrefix + key + " has duplicate values " +
                        value.stream().map(val -> "L" + val.getLine() + ":'" + val.getValue() + "'")
                                .collect(Collectors.joining(",")));
            }
        });

        return scanResult;
    }
}
