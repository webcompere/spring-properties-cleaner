package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.model.PropertiesFile;

import java.util.List;

public class Fixer {
    /**
     * Fix the files and write them out
     * @param propertiesFiles the files
     * @param results the results of the scan
     * @param writer the target writer
     * @return true unless it went wrong
     */
    public static boolean fix(List<PropertiesFile> propertiesFiles,
                              List<ScanResult> results,
                              Writer writer) {
        try {
            if (propertiesFiles.size() != results.size()) {
                throw new IllegalArgumentException("Mismatching inputs");
            }
            for (int i = 0; i < propertiesFiles.size(); i++) {
                fix(propertiesFiles.get(i), results.get(i));
            }

            writer.writeAll(propertiesFiles);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Apply fixes to the file
     * @param file file to fix
     * @param scanResult things found on the scan - duplicates etc
     */
    static void fix(PropertiesFile file, ScanResult scanResult) {
        scanResult.getDuplicateKeys().forEach(file::collapseIntoLast);
    }
}
