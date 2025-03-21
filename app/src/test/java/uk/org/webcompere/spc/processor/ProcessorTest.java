package uk.org.webcompere.spc.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;

import java.io.File;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SystemStubsExtension.class)
class ProcessorTest {
    private static final File EXAMPLE_WITH_DUPLICATE_PROPERTIES =
            Paths.get("src", "test", "resources", "example-with-duplicate.properties").toFile();

    @SystemStub
    private SystemErr systemErr;

    private Processor processor = new Processor();

    @Test
    void whenProcessingNonExistentFileThenError() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setRead("src/not a file");

        assertThat(processor.execute(args)).isFalse();

        assertThat(systemErr.getLines()).contains("src/not a file does not exist");
    }

    @Test
    void whenProcessingFileWithDuplicatesThenError() throws Exception {
        assertThat(processor.process(EXAMPLE_WITH_DUPLICATE_PROPERTIES, new SpcArgs())).isFalse();

        assertThat(systemErr.getLines())
                .contains("example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }
}