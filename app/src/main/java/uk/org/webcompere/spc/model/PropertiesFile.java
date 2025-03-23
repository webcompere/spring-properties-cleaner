package uk.org.webcompere.spc.model;

import com.beust.ah.A;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    /**
     * Convert the file back into lines
     * @return the file as series of lines - settings with preceding comments and then some trailing comments
     */
    public List<String> toLines() {
        return Stream.concat(settings.stream()
                        .flatMap(setting ->
                                Stream.concat(setting.getPrecedingComments().stream(),
                                        Stream.of(setting.getFullPath() + "=" + setting.getValue()))),
                trailingComments.stream()).collect(toList());
    }

    /**
     * Take a group of settings and collapse them into their last value - merging all comments
     * @param fullPath the path to take
     */
    public void collapseIntoLast(String fullPath) {
        int lastIndex = -1;
        List<String> preceedingComments = new ArrayList<>();
        for (int line = 0; line < settings.size(); line++) {
            Setting setting = settings.get(line);
            if (setting.getFullPath().equals(fullPath)) {
                preceedingComments.addAll(setting.getPrecedingComments());
                lastIndex = line;
            }
        }

        if (lastIndex != -1) {
            settings.get(lastIndex).setPrecedingComments(preceedingComments);
        }

        // go backwards and delete any of the predecessors
        for (int delete = lastIndex - 1; delete >= 0; delete--) {
            if (settings.get(delete).getFullPath().equals(fullPath)) {
                settings.remove(delete);
            }
        }
    }

    /**
     * Remove all errors, returning the ones we have before to the caller
     * @return all line errors
     */
    public List<LineError> extractErrors() {
        var errors = lineErrors;
        lineErrors = new ArrayList<>();
        return errors;
    }
}
