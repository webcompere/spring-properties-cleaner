package uk.org.webcompere.spc.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.spc.model.PropertiesFileFactory.createFile;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;
import uk.org.webcompere.spc.processor.sorting.AlphaNumericSort;

class ConverterTest {

    @Test
    void aPropertiesFileKeepsItsPathWhenNotYml() {
        File original = new File("/path/to/file.properties");
        assertThat(Converter.targetFile(original, false)).isEqualTo(new File("/path/to/file.properties"));
    }

    @Test
    void aPropertiesFileIsGivenAYmlSufficeWhenYml() {
        File original = new File("/path/to/file.properties");
        assertThat(Converter.targetFile(original, true)).isEqualTo(new File("/path/to/file.yml"));
    }

    @Test
    void aPropertiesFileIsMadeLowerCaseAndGivenAYmlSufficeWhenYml() {
        File original = new File("/path/to/File.PROPERTIES");
        assertThat(Converter.targetFile(original, true)).isEqualTo(new File("/path/to/file.yml"));
    }

    @Test
    void propertiesFileIsOutputAsLinesWhenNotYml() {
        PropertiesFile file = createFile("testfile", Map.of("server.port", "8080", "server.name", "myserver"));

        assertThat(Converter.toLines(file, SpcArgs.SortMode.none, false))
                .containsExactlyInAnyOrder("server.port=8080", "server.name=myserver");
    }

    @Test
    void propertiesFileIsOutputAsYml() {
        PropertiesFile file = createFile("testfile", Map.of("server.port", "8080", "server.name", "myserver"));
        file.sortSettings(AlphaNumericSort::alphaNumericSort);

        assertThat(Converter.toLines(file, SpcArgs.SortMode.sorted, true))
                .containsExactly("server:", "  name: myserver", "  port: 8080");
    }

    @Test
    void propertiesFileWithMultipleLevelsOfNestingIsOutputAsYml() {
        PropertiesFile file = createFile(
                "testfile",
                Map.of(
                        "server.port",
                        "8080",
                        "server.name",
                        "myserver",
                        "zebra.stripe.fan",
                        "true",
                        "zebra.stripe.detector",
                        "on",
                        "zebra.range",
                        "close"));
        file.sortSettings(AlphaNumericSort::alphaNumericSort);

        assertThat(Converter.toLines(file, SpcArgs.SortMode.sorted, true))
                .containsExactly(
                        "server:",
                        "  name: myserver",
                        "  port: 8080",
                        "zebra:",
                        "  range: close",
                        "  stripe:",
                        "    detector: on",
                        "    fan: true");
    }

    @Test
    void propertiesFileWithCommentIsOutputAsYml() {
        PropertiesFile file = createFile("testfile", Map.of("server.port", "8080", "server.name", "myserver"));
        file.add(new Setting(9, List.of("# this is the address"), "server.address", "127.0.0.1"));

        file.sortSettings(AlphaNumericSort::alphaNumericSort);

        assertThat(Converter.toLines(file, SpcArgs.SortMode.sorted, true))
                .containsExactly(
                        "server:",
                        "  # this is the address",
                        "  address: 127.0.0.1",
                        "  name: myserver",
                        "  port: 8080");
    }

    @Test
    void propertiesFileWithCommentIsOutputAsYmlInClustered() {
        LinkedHashMap<String, String> orderedItems = new LinkedHashMap<>();
        orderedItems.put("server.name", "myserver");
        orderedItems.put("server.port", "8080");

        PropertiesFile file = createFile("testfile", orderedItems);
        file.add(new Setting(9, List.of("# this is the address"), "server.address", "127.0.0.1"));

        Sorting.applySort(file, SpcArgs.SortMode.clustered);

        assertThat(Converter.toLines(file, SpcArgs.SortMode.clustered, true))
                .containsExactly(
                        "server:",
                        "  name: myserver",
                        "  port: 8080",
                        "  # this is the address",
                        "  address: 127.0.0.1");
    }

    @Test
    void propertiesFileWithTrailingCommentsOutputInYml() {
        PropertiesFile file = createFile("testfile", Map.of("server.port", "8080", "server.name", "myserver"));
        file.addTrailingComments(List.of("# trailing one", "", "# trailing two"));
        file.sortSettings(AlphaNumericSort::alphaNumericSort);

        assertThat(Converter.toLines(file, SpcArgs.SortMode.sorted, true))
                .containsExactly("server:", "  name: myserver", "  port: 8080", "# trailing one", "", "# trailing two");
    }
}
