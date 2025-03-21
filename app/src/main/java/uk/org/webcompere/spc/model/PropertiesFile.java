package uk.org.webcompere.spc.model;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Model of a file
 */
@Getter
public class PropertiesFile {
    private List<Setting> settings = new ArrayList<>();
    private List<String> trailingComments = new ArrayList<>();
    private File source;

    private List<LineError> lineErrors = new ArrayList<>();

    public PropertiesFile(File source) {
        this.source = source;
    }

    /**
     * Derive the short name from the original filename
     * @return the name
     */
    public String getName() {
        return source.getName();
    }

    /**
     * Add a setting read from the original file
     * @param setting the setting
     */
    public void add(Setting setting) {
        settings.add(setting);
    }

    /**
     * Store any comments from the footer of the file
     * @param trailingComments the last comment lines in the file
     */
    public void addTrailingComments(List<String> trailingComments) {
        this.trailingComments.addAll(trailingComments);
    }

    /**
     * Add an error about a line in the source file
     * @param lineNumber the original file's line number
     * @param line the original line
     */
    public void addError(int lineNumber, String line) {
        lineErrors.add(new LineError(lineNumber, line));
    }

    /**
     * Get the duplicate settings from a file
     * @return the duplicates by key
     */
    public Map<String, List<Setting>> getDuplicates() {
        return settings.stream().collect(groupingBy(Setting::getFullPath, toList()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
