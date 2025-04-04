package uk.org.webcompere.spc.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import java.io.File;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@ExtendWith(SystemStubsExtension.class)
class ProcessorTest {
    private static final File EXAMPLE_WITH_DUPLICATE_PROPERTIES =
            Paths.get("src", "test", "resources", "example-with-duplicate.properties").toFile();
    private static final File EXAMPLE_WITH_DUPLICATE_IDENTICAL_PROPERTIES =
            Paths.get("src", "test", "resources", "example-with-duplicate-identical.properties").toFile();
    private static final File EXAMPLE_DIRECTORY = Paths.get("src", "test", "resources").toFile();
    private static final File EXAMPLE_BROKEN =
            Paths.get("src", "test", "resources", "broken", "broken.properties").toFile();
    private static final File EXAMPLE_TELESCOPING_PROPERTIES =
            Paths.get("src", "test", "resources", "broken", "telescoping.properties").toFile();

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    private SpcArgs emptyArgs = new SpcArgs();

    private Processor processor = new Processor();

    @Test
    void whenProcessingNonExistentFileThenError() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setRead("src/not a file");

        assertThat(processor.execute(args)).isFalse();

        assertThat(systemErr.getLines()).contains("src/not a file does not exist");
    }

    @Test
    void whenScanningExampleWithFixAndDryRunThenFixAppears() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setAction(SpcArgs.Action.fix);
        args.setRead(EXAMPLE_WITH_DUPLICATE_PROPERTIES.getAbsolutePath());

        assertThat(processor.execute(args)).isTrue();

        assertThat(systemOut.getLines()).contains("property1=boo");
        assertThat(systemOut.getLines()).doesNotContain("property1=foo");
    }

    @Test
    void whenScanningWholeDirectoryThenErrorsAppear() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setPrefix("example");

        args.setRead(EXAMPLE_DIRECTORY.getAbsolutePath());

        assertThat(processor.execute(args)).isFalse();

        assertThat(systemErr.getLines())
                .contains("example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }

    @Test
    void whenProcessingBrokenFileWithDuplicatesThenError() throws Exception {
        assertThat(processor.process(EXAMPLE_BROKEN, emptyArgs))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains("broken.properties: non property 'aaaargh' on L1");
    }

    @Test
    void whenProcessingFileWithDuplicatesThenError() throws Exception {
        assertThat(processor.process(EXAMPLE_WITH_DUPLICATE_PROPERTIES, emptyArgs))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains("example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }

    @Test
    void whenProcessingFileWithDuplicateIdenticalPropertiesThenWarning() throws Exception {
        assertThat(processor.process(EXAMPLE_WITH_DUPLICATE_IDENTICAL_PROPERTIES, emptyArgs))
                .isTrue();

        assertThat(systemOut.getLines())
                .contains("example-with-duplicate-identical.properties: property1 has duplicate value 'foo' on L2,L7");
    }

    @Test
    void cannotProcessFileWithTelescopingPropertiesToYML() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setYml(true);
        args.setAction(SpcArgs.Action.fix);
        assertThat(processor.process(EXAMPLE_TELESCOPING_PROPERTIES, args))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains("Cannot convert to YML owing to telescoping properties");
    }

    @Test
    void cannotProcessDirectoryWithTelescopingPropertiesToYML() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setYml(true);
        args.setAction(SpcArgs.Action.fix);
        args.setPrefix("telescoping");
        assertThat(processor.processDirectory(EXAMPLE_TELESCOPING_PROPERTIES.getParentFile(), args))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains("Cannot convert to YML owing to telescoping properties");
    }
}