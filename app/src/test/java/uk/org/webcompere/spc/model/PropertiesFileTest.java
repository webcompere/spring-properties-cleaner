package uk.org.webcompere.spc.model;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}