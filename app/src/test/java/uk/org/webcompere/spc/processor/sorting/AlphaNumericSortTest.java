package uk.org.webcompere.spc.processor.sorting;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.spc.processor.sorting.AlphaNumericSort.alphaNumericSort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AlphaNumericSortTest {

    @ParameterizedTest
    @MethodSource("sortExamples")
    void givenDataShouldSort(List<String> input, List<String> expected) {
        List<String> sorted = new ArrayList<>(input);
        sorted.sort(AlphaNumericSort::alphaNumericSort);

        assertThat(sorted).isEqualTo(expected);
    }

    private static Stream<Arguments> sortExamples() {
        return Stream.of(
                Arguments.of(List.of(), List.of()),
                Arguments.of(List.of("", ""), List.of("", "")),
                Arguments.of(List.of("a1000", "a1000"), List.of("a1000", "a1000")),
                Arguments.of(List.of("BOB", "charlie", "BOB"), List.of("BOB", "BOB", "charlie")),
                Arguments.of(List.of("1", "A"), List.of("A", "1")),
                Arguments.of(List.of("1.", "0."), List.of("0.", "1.")),
                Arguments.of(List.of("0003", "01"), List.of("01", "0003")),
                Arguments.of(List.of("0003", "00000001"), List.of("00000001", "0003")),
                Arguments.of(List.of("1235", "1234"), List.of("1234", "1235")),
                Arguments.of(List.of("1234", "1234 "), List.of("1234", "1234 ")),
                Arguments.of(List.of("a", "c", "d"), List.of("a", "c", "d")),
                Arguments.of(List.of("a", "c2", "c1"), List.of("a", "c1", "c2")),
                Arguments.of(List.of("ab100", "c2", "ab2"), List.of("ab2", "ab100", "c2")),
                Arguments.of(List.of("ah2foo", "ah2foo", "ah2foo"), List.of("ah2foo", "ah2foo", "ah2foo")),
                Arguments.of(List.of("ah11foo", "ah1foo", "ah2foo"), List.of("ah1foo", "ah2foo", "ah11foo")),
                Arguments.of(List.of("ah1.foo", "ah1foo", "ah2foo"), List.of("ah1.foo", "ah1foo", "ah2foo")),
                Arguments.of(List.of("num 1.1.2", "num 0.1.2"), List.of("num 0.1.2", "num 1.1.2")),
                Arguments.of(List.of("12 b", "a b"), List.of("a b", "12 b")),
                Arguments.of(List.of("12 b", "a b", "c b"), List.of("a b", "c b", "12 b")),
                Arguments.of(List.of("ah1.1foo", "ah1foo", "ah2foo"), List.of("ah1foo", "ah1.1foo", "ah2foo")));
    }

    @Test
    void numberIsLaterThanLetter() {
        assertThat(alphaNumericSort("12", "a")).isGreaterThan(0);
        assertThat(alphaNumericSort("a a12", "a a")).isGreaterThan(0);
    }

    @Test
    void longerStringIsLater() {
        assertThat(alphaNumericSort("aa1", "aa")).isGreaterThan(0);
        assertThat(alphaNumericSort("ab12a9", "ab12a")).isGreaterThan(0);

        assertThat(alphaNumericSort("aa", "aa1")).isLessThan(0);
        assertThat(alphaNumericSort("ab12a", "ab12a0")).isLessThan(0);
    }
}
