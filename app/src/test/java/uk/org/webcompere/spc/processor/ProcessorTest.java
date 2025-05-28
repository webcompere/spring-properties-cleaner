package uk.org.webcompere.spc.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

@ExtendWith(SystemStubsExtension.class)
class ProcessorTest {
    private static final File EXAMPLE_WITH_DUPLICATE_PROPERTIES = Paths.get(
                    "src", "test", "resources", "example-with-duplicate.properties")
            .toFile();
    private static final File EXAMPLE_WITH_DUPLICATE_IDENTICAL_PROPERTIES = Paths.get(
                    "src", "test", "resources", "example-with-duplicate-identical.properties")
            .toFile();

    private static final File EXAMPLE_WITH_NO_DUPLICATE_PROPERTIES = Paths.get(
                    "src", "test", "resources", "example-with-no-duplicate.properties")
            .toFile();

    private static final File EXAMPLE_DIRECTORY =
            Paths.get("src", "test", "resources").toFile();
    private static final File EXAMPLE_BROKEN =
            Paths.get("src", "test", "resources", "broken", "broken.properties").toFile();
    private static final File EXAMPLE_TELESCOPING_PROPERTIES = Paths.get(
                    "src", "test", "resources", "broken", "telescoping.properties")
            .toFile();

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    private SpcArgs emptyArgs = new SpcArgs();

    private Processor processor;

    @BeforeEach
    void beforeEach() {
        processor = new Processor();
    }

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
    void whenScanningExampleWithFixAndDryRunThenFixAppearsInAlternativeLogs() throws Exception {
        List<String> logs = new ArrayList<>();
        Processor otherProcessor = new Processor(logs::add, logs::add);

        SpcArgs args = new SpcArgs();
        args.setAction(SpcArgs.Action.fix);
        args.setRead(EXAMPLE_WITH_DUPLICATE_PROPERTIES.getAbsolutePath());

        assertThat(otherProcessor.execute(args)).isTrue();

        assertThat(logs).contains("property1=boo");
        assertThat(logs).doesNotContain("property1=foo");
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
        assertThat(processor.process(EXAMPLE_BROKEN, emptyArgs)).isFalse();

        assertThat(systemErr.getLines()).contains("broken.properties: non property 'aaaargh' on L1");
    }

    @Test
    void whenProcessingFileWithDuplicatesThenError() throws Exception {
        assertThat(processor.process(EXAMPLE_WITH_DUPLICATE_PROPERTIES, emptyArgs))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains("example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }

    @Test
    void whenProcessingFileWithIdenticalDuplicatesThenIsError() throws Exception {
        assertThat(processor.process(EXAMPLE_WITH_DUPLICATE_IDENTICAL_PROPERTIES, emptyArgs))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains(
                        "example-with-duplicate-identical.properties: property1 has duplicate values L2:'foo',L7:'foo'");
    }

    @Test
    void cannotProcessFileWithTelescopingPropertiesToYML() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setYml(true);
        args.setAction(SpcArgs.Action.fix);
        assertThat(processor.process(EXAMPLE_TELESCOPING_PROPERTIES, args)).isFalse();

        assertThat(systemErr.getLines()).contains("Cannot convert to YML owing to telescoping properties");
    }

    @Test
    void cannotProcessDirectoryWithTelescopingPropertiesToYML() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setYml(true);
        args.setAction(SpcArgs.Action.fix);
        args.setPrefix("telescoping");
        assertThat(processor.processDirectory(EXAMPLE_TELESCOPING_PROPERTIES.getParentFile(), args))
                .isFalse();

        assertThat(systemErr.getLines()).contains("Cannot convert to YML owing to telescoping properties");
    }

    @Test
    void scanningFileThatIsOkIsNoError() throws Exception {
        assertThat(processor.process(EXAMPLE_WITH_NO_DUPLICATE_PROPERTIES, emptyArgs))
                .isTrue();
    }

    @Test
    void scanningFileThatIsOkWithChangesForSortingIsError() throws Exception {
        SpcArgs args = new SpcArgs();
        args.setAction(SpcArgs.Action.scan);
        args.setSort(SpcArgs.SortMode.sorted);

        assertThat(processor.process(EXAMPLE_WITH_NO_DUPLICATE_PROPERTIES, args))
                .isFalse();

        assertThat(systemErr.getLines())
                .contains("File 'example-with-no-duplicate.properties' does not meet standard - have you run fix?");
    }
}
