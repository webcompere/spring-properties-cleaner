package uk.org.webcompere.spc.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

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
        assertThat(file.toLines()).isEmpty();
    }

    @Test
    void fileWithOneSettingCreatesOneLine() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));

        assertThat(file.toLines()).containsExactly("server.port=8080");
    }

    @Test
    void fileWithOneSettingAndACommentCreatesTwoLines() {
        file.add(new Setting(2, List.of("# comment"), "a", "b"));

        assertThat(file.toLines()).containsExactly("# comment", "a=b");
    }

    @Test
    void fileWithOneSettingAndTrailingCommentsCreatesLines() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.addTrailingComments(List.of("", "# foobar"));

        assertThat(file.toLines()).containsExactly("server.port=8080", "", "# foobar");
    }

    @Test
    void collapseSettingsIntoLast() {
        file.add(new Setting(1, List.of(), "server.port", "8080"));
        file.add(new Setting(2, List.of(), "server.port", "8080"));

        file.collapseIntoLast("server.port");

        assertThat(file.toLines()).containsExactly("server.port=8080");
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
}
