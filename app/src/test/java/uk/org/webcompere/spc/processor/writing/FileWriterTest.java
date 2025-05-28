package uk.org.webcompere.spc.processor.writing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

class FileWriterTest {

    @TempDir
    private File tempDir;

    private FileWriter fileWriter = new FileWriter();

    @Test
    void willWritePropertiesFileToDisk() throws Exception {
        PropertiesFile file = new PropertiesFile(new File(tempDir, "application.props"));
        file.add(new Setting(1, List.of(), "foo", "bar"));

        fileWriter.writeAll(List.of(file), SpcArgs.SortMode.none, SpcArgs.WhiteSpaceMode.preserve, false);

        assertThat(file.getSource()).hasContent("foo=bar");
    }

    @Test
    void willWritePropertiesFileWithLeadingWhitespaceToDisk() throws Exception {
        PropertiesFile file = new PropertiesFile(new File(tempDir, "application.props"));
        file.add(new Setting(3, List.of("", "# doobie"), "foo", "bar"));

        fileWriter.writeAll(List.of(file), SpcArgs.SortMode.none, SpcArgs.WhiteSpaceMode.preserve, false);

        assertThat(file.getSource()).hasContent("\n# doobie\nfoo=bar");
    }
}
