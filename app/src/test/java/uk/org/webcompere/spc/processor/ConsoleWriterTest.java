package uk.org.webcompere.spc.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;

@ExtendWith(SystemStubsExtension.class)
class ConsoleWriterTest {

    @SystemStub
    private SystemOut systemOut;

    @Test
    void willWritePropertiesFileToConsole() throws Exception {
        var consoleWriter = new ConsoleWriter();
        PropertiesFile file = new PropertiesFile(new File("/src/application.props"));
        file.add(new Setting(1, List.of(), "foo", "bar"));

        consoleWriter.writeAll(List.of(file), SpcArgs.SortMode.none, false);

        assertThat(systemOut.getLines())
                .containsExactly("--- START File: /src/application.props ---", "foo=bar", "--- ENDS ---");
    }
}
