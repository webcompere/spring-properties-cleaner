package uk.org.webcompere.spc.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SectionBreakerTest {
    private SectionBreaker breaker = new SectionBreaker();

    @Test
    void firstItemHasNoBreak() {
        assertThat(breaker.apply("foo")).isEmpty();
    }

    @Test
    void noBreaksBetweenSimilarItems() {
        assertThat(breaker.apply("foo.bar")).isEmpty();
        assertThat(breaker.apply("foo.bong")).isEmpty();
        assertThat(breaker.apply("foo.bing")).isEmpty();
        assertThat(breaker.apply("foo.bung")).isEmpty();
    }

    @Test
    void breaksAtDifferentItems() {
        assertThat(breaker.apply("foo.bar")).isEmpty();
        assertThat(breaker.apply("foo.bong")).isEmpty();

        assertThat(breaker.apply("badda.bing")).isNotEmpty();
        assertThat(breaker.apply("badda.bung")).isEmpty();
    }

    @Test
    void noBreaksBetweenBlanks() {
        assertThat(breaker.apply("")).isEmpty();
        assertThat(breaker.apply("")).isEmpty();
        assertThat(breaker.apply("")).isEmpty();
        assertThat(breaker.apply("")).isEmpty();
    }

    @Test
    void noBreaksBetweenItemsWithNoDots() {
        assertThat(breaker.apply("abc")).isEmpty();
        assertThat(breaker.apply("abc")).isEmpty();
        assertThat(breaker.apply("abc")).isEmpty();
        assertThat(breaker.apply("abc")).isEmpty();
    }

    @Test
    void breaksBetweenDifferentItemsWithNoDots() {
        assertThat(breaker.apply("abc")).isEmpty();
        assertThat(breaker.apply("def")).isNotEmpty();
        assertThat(breaker.apply("ghi")).isNotEmpty();
        assertThat(breaker.apply("jkl")).isNotEmpty();
    }
}
