package uk.org.webcompere.spc.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FileWriterTest {

    @TempDir
    private File tempDir;

    private FileWriter fileWriter = new FileWriter();

    @Test
    void willWritePropertiesFileToConsole() throws Exception {
        PropertiesFile file = new PropertiesFile(new File(tempDir, "application.props"));
        file.add(new Setting(1, List.of(), "foo", "bar"));

        fileWriter.writeAll(List.of(file), SpcArgs.SortMode.none, false);

        assertThat(file.getSource()).hasContent("foo=bar");
    }

}