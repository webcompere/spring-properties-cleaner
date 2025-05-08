package uk.org.webcompere.spc.processor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;

/**
 * For writing fixed files
 */
public interface Writer {

    /**#
     * Write all the files
     * @param propertiesFiles files to write
     * @param sortMode the sort mode used on the files
     * @param isYml to write in YML?
     * @throws IOException on file error
     */
    default void writeAll(List<PropertiesFile> propertiesFiles, SpcArgs.SortMode sortMode, boolean isYml)
            throws IOException {
        for (var file : propertiesFiles) {
            write(Converter.targetFile(file.getSource(), isYml), Converter.toLines(file, sortMode, isYml));
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
