package uk.org.webcompere.spc.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.spc.cli.SpcArgs;

class PropertiesFileTest {
    private PropertiesFile file = new PropertiesFile(new File("properties"));

    @Test
    void emptyFileHasNoDuplicates() {
        assertThat(file.getDuplicates()).isEmpty();
    }

    @Test
    void fileWithTwoDifferentSettingsHasNoDuplicates() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.host", "localhost"));
        assertThat(file.getDuplicates()).isEmpty();
    }

    @Test
    void fileWithDuplicatesCanDetectThem() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8081"));
        assertThat(file.getDuplicates()).containsKeys("server.port");
    }

    @Test
    void fileWithDuplicatesHasThemInOrder() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8081"));
        assertThat(file.getDuplicates().get("server.port").stream().map(Setting::getValue))
                .containsExactly("8080", "8081");
    }

    @Test
    void emptyFileConvertsToNoLines() {
        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve)).isEmpty();
    }

    @Test
    void fileWithOneSettingCreatesOneLine() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve)).containsExactly("server.port=8080");
    }

    @Test
    void fileWithOneSettingAndACommentCreatesTwoLines() {
        file.add(new Setting(2, List.of("# comment"), "a", "b"));

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve)).containsExactly("# comment", "a=b");
    }

    @Test
    void fileWithOneSettingACommentAndWhitespaceCreatesTwoLines() {
        file.add(new Setting(2, List.of("", "# comment"), "a", "b"));

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve)).containsExactly("", "# comment", "a=b");
    }

    @Test
    void fileWithOneSettingACommentAndWhitespaceWhenWhiteSpaceIsFilteredRemovesIt() {
        file.add(new Setting(2, List.of("", "# comment", "", "# comment 2"), "a", "b"));

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.remove)).containsExactly("# comment", "# comment 2", "a=b");
    }

    @Test
    void willInsertSectionBreakBetweenSections() {
        file.add(new Setting(1, List.of("", "", ""), "spring.server", "b"));
        file.add(new Setting(2, List.of("", "", ""), "spring.port", "8080"));
        file.add(new Setting(2, List.of("", "", ""), "redis.port", "9000"));

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.section))
                .containsExactly("spring.server=b", "spring.port=8080", "", "redis.port=9000");
    }

    @Test
    void fileWithOneSettingAndTrailingCommentsCreatesLines() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.addTrailingComments(List.of("", "# foobar"));

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve)).containsExactly("server.port=8080", "", "# foobar");
    }

    @Test
    void collapseSettingsIntoLast() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8080"));

        file.collapseIntoLast("server.port");

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve)).containsExactly("server.port=8080");
    }

    @Test
    void removeIf() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8080"));
        file.add(new Setting(3, List.of(), "server.port", "8081"));

        file.removeIf("server.port", "8080");

        assertThat(file.getSettings()).hasSize(1);
        assertThat(file.getLast("server.port")).hasValue("8081");
    }

    @Test
    void duplicatesComeInFileOrder() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8080"));
        file.add(new Setting(3, List.of(), "app.name", "myApp"));
        file.add(new Setting(4, List.of(), "app.name", "myApp"));
        file.add(new Setting(5, List.of(), "local.host", "localhost"));
        file.add(new Setting(6, List.of(), "local.host", "localhost"));

        assertThat(file.getDuplicates().entrySet().stream().map(Map.Entry::getKey))
                .containsExactly("server.port", "app.name", "local.host");
    }

    @Test
    void mapToleratesDuplicates() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8080"));
        file.add(new Setting(3, List.of(), "app.name", "myApp"));
        file.add(new Setting(4, List.of(), "app.name", "myApp"));
        file.add(new Setting(5, List.of(), "local.host", "localhost"));
        file.add(new Setting(6, List.of(), "local.host", "localhost"));

        assertThat(file.getAsMap())
                .containsEntry("server.port", "8080")
                .containsEntry("app.name", "myApp")
                .containsEntry("local.host", "localhost");
    }

    @Test
    void fixUpsCanBeApplied() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8080"));
        file.add(new Setting(3, List.of(), "app.name", "myApp"));
        file.add(new Setting(4, List.of(), "app.name", "myApp"));
        file.add(new Setting(5, List.of(), "local.host", "localhost"));
        file.add(new Setting(6, List.of(), "local.host", "localhost"));

        file.applyFixups(Map.of("server.port", "8081", "none", "foo"));

        assertThat(file.getAsMap())
                .containsEntry("server.port", "8081")
                .containsEntry("app.name", "myApp")
                .containsEntry("local.host", "localhost");
    }
}
