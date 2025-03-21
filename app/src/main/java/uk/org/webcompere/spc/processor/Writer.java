package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.model.PropertiesFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * For writing fixed files
 */
public interface Writer {

    /**#
     * Write all the files
     * @param propertiesFiles files to write
     * @throws IOException on file error
     */
    default void writeAll(List<PropertiesFile> propertiesFiles) throws IOException {
        for (var file: propertiesFiles) {
            write(file.getSource(), file.toLines());
        }
    }

    /**
     * Write all the fixed files
     * @param file to write
     * @param lines contents of the file
     * @throws IOException on file error
     */
    void write(File file, List<String> lines) throws IOException;
}
