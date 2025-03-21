package uk.org.webcompere.spc.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of a file
 */
@Getter
public class PropertiesFile {
    private List<Setting> settings = new ArrayList<>();
    private List<String> trailingComments = new ArrayList<>();
    private String name;
    private List<LineError> lineErrors = new ArrayList<>();

    public PropertiesFile(String name) {
        this.name = name;
    }

    public void add(Setting setting) {
        settings.add(setting);
    }

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
}
