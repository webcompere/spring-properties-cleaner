package uk.org.webcompere.spc.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.spc.parser.Lines.streamPairs;

class LinesTest {

    @Test
    void emptyListIsStreamedToEmptyPairs() {
        List<String> empty = List.of();

        assertThat(streamPairs(empty)).isEmpty();
    }

    @Test
    void singleItemListIsStreamedToEmptyPairs() {
        List<String> empty = List.of("foo");

        assertThat(streamPairs(empty)).isEmpty();
    }

    @Test
    void singlePairListIsStreamedToOneElementOfThatPair() {
        List<String> empty = List.of("foo", "bar");

        assertThat(streamPairs(empty)).containsExactly(new Lines.Pair<>("foo", "bar"));
    }

    @Test
    void oddPairListIsStreamedToEachPairing() {
        List<String> empty = List.of("foo", "bar", "baz");

        assertThat(streamPairs(empty)).containsExactly(new Lines.Pair<>("foo", "bar"), new Lines.Pair<>("bar", "baz"));
    }

    @Test
    void multiPairListIsStreamedToEachPairing() {
        List<String> empty = List.of("foo", "bar", "baz", "boz");

        assertThat(streamPairs(empty)).containsExactly(
                new Lines.Pair<>("foo", "bar"),
                new Lines.Pair<>("bar", "baz"),
                new Lines.Pair<>("baz", "boz"));
    }
}