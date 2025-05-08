package uk.org.webcompere.spc.parser;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class Lines {
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
        return IntStream.range(0, list.size() - 1).mapToObj(i -> new Pair<>(list.get(i), list.get(i + 1)));
    }
}
