package uk.org.webcompere.spc.processor.writing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for comparing files we want to save with their originals.
 */
public class StoringWriter implements Writer {
    private Map<File, List<String>> stored = new HashMap<>();

    @Override
    public void write(File file, List<String> lines) {
        stored.put(file, lines);
    }

    /**
     * Which files on disk are different to the files we were given
     * @return a list of filenames with differences
     */
    public List<File> whichAreDifferent() {
        return stored.entrySet().stream()
                .filter(entry -> hasDifferencesFromDisk(entry.getKey(), entry.getValue()))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    private static boolean hasDifferencesFromDisk(File file, List<String> remembered) {
        if (!file.exists()) {
            // different if the body is not empty when the file doesn't exist
            return !remembered.isEmpty();
        }

        try (var lines = Files.lines(file.toPath())) {
            List<String> onDisk = lines.collect(Collectors.toList());
            return !onDisk.equals(remembered);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
