package uk.org.webcompere.spc.processor.sorting;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.spc.processor.sorting.AlphaNumericSort.createSort;

class AlphaNumericSortTest {

    @ParameterizedTest
    @MethodSource("sortExamples")
    void givenDataShouldSort(List<String> input, List<String> expected) {
        List<String> sorted = new ArrayList<>(input);
        sorted.sort(createSort());

        assertThat(sorted).isEqualTo(expected);
    }

    private static Stream<Arguments> sortExamples() {
        return Stream.of(
                Arguments.of(List.of(), List.of()),
                Arguments.of(List.of("", ""), List.of("", "")),
                Arguments.of(List.of("1", "A"), List.of("A", "1")),
                Arguments.of(List.of("0003", "01"), List.of("01", "0003")),
                Arguments.of(List.of("0003", "00000001"), List.of("00000001", "0003")),
                Arguments.of(List.of("1235", "1234"), List.of("1234", "1235")),
                Arguments.of(List.of("1234", "1234 "), List.of("1234", "1234 ")),
                Arguments.of(List.of("a", "c", "d"), List.of("a", "c", "d")),
                Arguments.of(List.of("a", "c2", "c1"), List.of("a", "c1", "c2")),
                Arguments.of(List.of("ab100", "c2", "ab2"), List.of("ab2", "ab100", "c2")),
                Arguments.of(List.of("ah11foo", "ah1foo","ah2foo"), List.of("ah1foo", "ah2foo","ah11foo")),
                Arguments.of(List.of("ah1.foo", "ah1foo","ah2foo"), List.of("ah1.foo", "ah1foo", "ah2foo")),
                Arguments.of(List.of("ah1.1foo", "ah1foo","ah2foo"), List.of("ah1foo", "ah1.1foo", "ah2foo"))
        );
    }
}