package uk.org.webcompere.spc.processor.writing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.testgadgets.testdatafactory.TestData;
import uk.org.webcompere.testgadgets.testdatafactory.TestDataFactory;

@TestDataFactory(path = "files")
class StoringWriterTest {
    private static File TEST_FILE =
            Paths.get("src", "test", "resources", "files", "test.txt").toFile();
    private static File TEST_FILE_NON_EXISTENT =
            Paths.get("src", "test", "resources", "files", "what.txt").toFile();

    private StoringWriter storingWriter = new StoringWriter();

    @Test
    void whenAddingAFileThatsIdenticalThenNoDifferences(@TestData("test.txt") String[] test) {
        storingWriter.write(TEST_FILE, Arrays.asList(test));

        assertThat(storingWriter.whichAreDifferent()).isEmpty();
    }

    @Test
    void whenAddingAFileThatsDifferentThenDifferences() {
        storingWriter.write(TEST_FILE, List.of("Boy", "This", "Changed"));

        assertThat(storingWriter.whichAreDifferent()).hasSize(1);
    }

    @Test
    void whenAddingAFileThatsNonExistent() {
        storingWriter.write(TEST_FILE_NON_EXISTENT, List.of("Boy", "This", "Changed"));

        assertThat(storingWriter.whichAreDifferent()).hasSize(1);
    }

    @Test
    void whenAddingABlankFileThatsNonExistentThenNotADifference() {
        storingWriter.write(TEST_FILE_NON_EXISTENT, List.of());

        assertThat(storingWriter.whichAreDifferent()).isEmpty();
    }
}
