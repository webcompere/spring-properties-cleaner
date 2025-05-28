package uk.org.webcompere.spc.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.spc.model.PropertiesFileFactory.createFile;
import static uk.org.webcompere.spc.processor.Scanner.scanForIssues;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScannerTest {

    @Test
    void whenPropertiesFileHasNoTelescopingPropertiesThenNoneFound() {
        var file = createFile("foo", Map.of("server.port", "8080", "local.host", "localhost"));

        var result = scanForIssues(file);

        assertThat(result.isTelescopingProperties()).isFalse();
    }

    @Test
    void whenPropertiesFileHasATelescopingProperty() {
        var file = createFile("foo", Map.of("server.port", "8080", "local.host", "localhost", "server", "myserver"));

        var result = scanForIssues(file);

        assertThat(result.isTelescopingProperties()).isTrue();
        assertThat(result.getWarnings()).contains("foo: property 'server' telescopes into 'server.port'");
    }
}
