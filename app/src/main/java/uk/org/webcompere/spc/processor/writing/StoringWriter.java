package uk.org.webcompere.spc.processor.writing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoringWriter implements Writer {
    private Map<File, List<String>> stored = new HashMap<>();

    @Override
    public void write(File file, List<String> lines) throws IOException {
        stored.put(file, lines);
    }

    public List<File> whichAreDifferent() {
        return stored.entrySet().stream()
                .filter(entry -> hasDifferencesFromDisk(entry.getKey(), entry.getValue()))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    private static boolean hasDifferencesFromDisk(File file, List<String> remembered) {
        try (var lines = Files.lines(file.toPath())) {
            List<String> onDisk = lines.collect(Collectors.toList());
            return !onDisk.equals(remembered);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
