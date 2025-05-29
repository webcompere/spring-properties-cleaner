package uk.org.webcompere.spc.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class InlinerTest {

    @Test
    void inlinerFindsNothingInEmptyMap() {
        Inliner inliner = new Inliner("foo", "bar");
        assertThat(inliner.scanForInlining(Map.of())).isEmpty();
    }

    @Test
    void inlinerFindsFixupsFromMap() {
        Inliner inliner = new Inliner("foo", "bar");

        assertThat(inliner.scanForInlining(Map.of("foo", "FOOEY", "user", "bar${foo}")))
                .containsExactlyInAnyOrderEntriesOf(Map.of("foo", "barFOOEY", "user", "${foo}"));
    }

    @Test
    void inlinerFindsFixupsFromMapIncludingSuffix() {
        Inliner inliner = new Inliner("foo", "bar");

        assertThat(inliner.scanForInlining(Map.of("foo", "FOOEY", "user", "bar${foo}/path")))
                .containsExactlyInAnyOrderEntriesOf(Map.of("foo", "barFOOEY", "user", "${foo}/path"));
    }

    @Test
    void inlinerFindsMultipleFixupsFromMapIncludingSuffix() {
        Inliner inliner = new Inliner("foo", "bar");

        assertThat(inliner.scanForInlining(
                        Map.of("foo", "FOOEY", "user1", "bar${foo}/path1", "user2", "bar${foo}/path2")))
                .containsExactlyInAnyOrderEntriesOf(
                        Map.of("foo", "barFOOEY", "user1", "${foo}/path1", "user2", "${foo}/path2"));
    }

    @Test
    void inlinerFindsNoFixupsWhenInconsistent() {
        Inliner inliner = new Inliner("foo", "bar?");

        assertThat(inliner.scanForInlining(
                        Map.of("foo", "FOOEY", "user1", "bar${foo}/path1", "user2", "ba${foo}/path1")))
                .isEmpty();
    }

    @Test
    void inlinerFindsNoFixupsWhenPlaceholderUsedWithoutMatchingPrefix() {
        Inliner inliner = new Inliner("foo", "bar?");

        assertThat(inliner.scanForInlining(
                        Map.of("foo", "FOOEY", "user1", "bar${foo}/path1", "user2", "bong${foo}/path1")))
                .isEmpty();
    }

    @Test
    void inlinerCanUseHttpsRegex() {
        Inliner inliner = new Inliner("foo", "https?://");

        assertThat(inliner.scanForInlining(
                        Map.of("foo", "FOOEY", "user1", "https://${foo}/path1", "user2", "https://${foo}/path2")))
                .containsExactlyInAnyOrderEntriesOf(
                        Map.of("foo", "https://FOOEY", "user1", "${foo}/path1", "user2", "${foo}/path2"));
    }
}
