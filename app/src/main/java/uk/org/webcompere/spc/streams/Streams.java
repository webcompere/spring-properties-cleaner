package uk.org.webcompere.spc.streams;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Streams {
    @SafeVarargs
    public static <T> Stream<T> concatAll(Stream<T>... streams) {
        return Arrays.stream(streams).flatMap(Function.identity());
    }
}
