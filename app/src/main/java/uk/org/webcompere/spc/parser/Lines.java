package uk.org.webcompere.spc.parser;

import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

public class Lines {
    /**
     * Remove the blanks from some lines
     * @param lines the lines
     * @return the non blank oneS
     */
    public static List<String> noBlankLines(List<String> lines) {
        return lines.stream().filter(not(String::isBlank)).collect(toList());
    }

    public static boolean allTheSame(Stream<String> lines) {
        return lines.distinct().count() <= 1;
    }
}
