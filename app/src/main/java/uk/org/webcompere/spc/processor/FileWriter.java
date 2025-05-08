package uk.org.webcompere.spc.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Write the files to a real place
 */
public class FileWriter implements Writer {

    @Override
    public void write(File file, List<String> lines) throws IOException {
        Files.writeString(file.toPath(), String.join("\n", lines));
    }
}
