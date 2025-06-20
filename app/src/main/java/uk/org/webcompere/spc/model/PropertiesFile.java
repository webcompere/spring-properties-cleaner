package uk.org.webcompere.spc.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static uk.org.webcompere.spc.streams.Streams.concatAll;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Getter;
import uk.org.webcompere.spc.cli.SpcArgs;

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
        return settings.stream()
                .collect(groupingBy(Setting::getFullPath, LinkedHashMap::new, toList()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    /**
     * Get a single property from the file
     * @param fullPath the path of the property
     * @return the value of the property or null
     */
    public String get(String fullPath) {
        return settings.stream()
                .filter(setting -> setting.getFullPath().equals(fullPath))
                .map(Setting::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Build the properties into a map - this will collapse duplicates randomly
     * so best done after a de-duping
     * @return a map of full path to value
     */
    public Map<String, String> getAsMap() {
        Map<String, String> map = new HashMap<>();
        for (Setting setting : settings) {
            map.put(setting.getFullPath(), setting.getValue());
        }
        return map;
    }

    /**
     * Make changes to the given properties. If we have them in this file, then we'll amend them
     * @param propertiesToFix the fix up to apply
     */
    public void applyFixups(Map<String, String> propertiesToFix) {
        for (Setting setting : settings) {
            if (propertiesToFix.containsKey(setting.getFullPath())) {
                setting.setValue(propertiesToFix.get(setting.getFullPath()));
            }
        }
    }

    /**
     * Convert the file back into lines
     * @param whiteSpaceMode the way to handle whitespace in the file
     * @return the file as series of lines - settings with preceding comments and then some trailing comments
     */
    public List<String> toLines(SpcArgs.WhiteSpaceMode whiteSpaceMode) {
        Function<String, Optional<String>> lineInserter =
                whiteSpaceMode == SpcArgs.WhiteSpaceMode.section ? new SectionBreaker() : line -> Optional.empty();
        return Stream.concat(
                        settings.stream()
                                .flatMap(setting -> concatAll(
                                        lineInserter.apply(setting.getFullPath()).stream(),
                                        getCommentsStream(whiteSpaceMode, setting),
                                        Stream.of(setting.getFullPath() + "=" + setting.getValue()))),
                        trailingComments.stream())
                .collect(toList());
    }

    private Stream<String> getCommentsStream(SpcArgs.WhiteSpaceMode whiteSpaceMode, Setting setting) {
        return setting.getPrecedingComments().stream().filter(shouldKeepCommentLine(whiteSpaceMode));
    }

    private static Predicate<String> shouldKeepCommentLine(SpcArgs.WhiteSpaceMode whiteSpaceMode) {
        return line -> whiteSpaceMode == SpcArgs.WhiteSpaceMode.preserve || !line.isBlank();
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

    /**
     * Apply a sort to the keys of the settings
     * @param sort the comparator of the sort
     */
    public void sortSettings(Comparator<CharSequence> sort) {
        settings.sort(Comparator.comparing(Setting::getFullPath, sort));
    }

    /**
     * Allow the settings to be rewritten
     * @param rewriter function to process settings
     */
    public void rewriteSettings(Function<List<Setting>, List<Setting>> rewriter) {
        settings = rewriter.apply(settings);
    }

    /**
     * Find the last value of a property by its full path
     * @param path path of the property
     * @return the value or empty if not found
     */
    public Optional<String> getLast(String path) {
        return IntStream.range(0, settings.size())
                .mapToObj(i -> settings.get(settings.size() - 1 - i))
                .filter(setting -> setting.getFullPath().equals(path))
                .map(Setting::getValue)
                .findFirst();
    }

    /**
     * Remove the property with this name
     * @param path the path to remove
     */
    public void remove(String path) {
        remove(setting -> setting.getFullPath().equals(path));
    }

    /**
     * Remove the property with this name if it contains the expected value
     * @param path the path to remove
     * @param expectedValue the expectedValue to remove, otherwise leave it alone
     */
    public void removeIf(String path, String expectedValue) {
        remove(setting ->
                setting.getFullPath().equals(path) && setting.getValue().equals(expectedValue));
    }

    private void remove(Predicate<Setting> predicate) {
        int i = 0;
        while (i < settings.size()) {
            if (predicate.test(settings.get(i))) {
                settings.remove(i);
            } else {
                i++;
            }
        }
    }
}
