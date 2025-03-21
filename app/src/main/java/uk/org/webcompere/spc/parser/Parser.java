package uk.org.webcompere.spc.parser;

import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.org.webcompere.spc.parser.Lines.noBlankLines;

/**
 * Parses a properties file line by line
 */
public class Parser {
    private static final Pattern PROPERTY_RECOGNISER = Pattern.compile("([a-zA-Z0-9_.-]+)=(.*)");
    private static final String COMMENT_CHAR = "#";

    private PropertiesFile target;
    private int lineNumber = 0;

    private List<String> pendingComments = new ArrayList<>();

    public Parser(PropertiesFile target) {
        this.target = target;
    }

    /**
     * Parse the next line
     * @param line the line to parse
     */
    public void parse(String line) {
        lineNumber++;

        if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
            pendingComments.add(line);
        }
        Matcher matcher = PROPERTY_RECOGNISER.matcher(line);
        if (!matcher.matches()) {
            target.addError(lineNumber, line);
        } else {
            target.add(new Setting(lineNumber, noBlankLines(pendingComments), matcher.group(1), matcher.group(2)));
            pendingComments = new ArrayList<>();
        }
    }

    /**
     * Finished parsing, so flush
     */
    public void close() {
        target.addTrailingComments(pendingComments);
    }
}
