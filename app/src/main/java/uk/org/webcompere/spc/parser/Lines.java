package uk.org.webcompere.spc.parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;
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

    /**
     * Are all these lines the same?
     * @param lines the lines to check
     * @return true if all the same or none
     */
    public static boolean allTheSame(Stream<String> lines) {
        return lines.distinct().count() <= 1;
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class Pair<T> {
        private T first;
        private T second;
    }

    /**
     * Convert a list into a set of pairs for review
     * @param list the list of items to review in pairs
     * @return a stream of pairs
     * @param <T> the type of the items
     */
    public static <T> Stream<Pair<T>> streamPairs(List<T> list) {
        return IntStream.range(0, list.size() - 1)
                .mapToObj(i -> new Pair<>(list.get(i), list.get(i+1)));
    }
}
