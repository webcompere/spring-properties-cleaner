package uk.org.webcompere.spc.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;
import uk.org.webcompere.spc.processor.writing.Writer;

@ExtendWith(MockitoExtension.class)
class FixerTest {

    private PropertiesFile file = new PropertiesFile(new File("foo"));
    private int lineNumber = 1;

    @Mock
    private Writer writer;

    @Test
    void mergesDuplicates() {
        addLine("key", "value");
        addLine("key", "value");

        Fixer.fix(file, new SpcArgs());

        assertThat(file.getSettings().size()).isEqualTo(1);

        assertThat(file.getSettings().get(0).getLine()).isEqualTo(2);
        assertThat(file.getSettings().get(0).getFullPath()).isEqualTo("key");
        assertThat(file.getSettings().get(0).getValue()).isEqualTo("value");
    }

    @Test
    void putsErrorLinesAtTheEndOfTheErrorsFooter() {

        file.addError(1, "broken");
        file.addError(2, "also broken");
        file.addTrailingComments(List.of("# foo", "# bar"));

        Fixer.fix(file, new SpcArgs());

        assertThat(file.getLineErrors()).isEmpty();
        assertThat(file.getTrailingComments()).containsExactly("# foo", "# bar", "# 1: broken", "# 2: also broken");
    }

    @Test
    void whenNoSortThenFixerDoesNotChangeOrderOfKeys() {
        addLine("zebra", "crossing");
        addLine("anteater", "nosey");

        Fixer.fix(file, new SpcArgs());

        assertThat(file.getSettings().stream().map(Setting::getFullPath)).containsExactly("zebra", "anteater");
    }

    @Test
    void whenSortThenFixerChangesOrderOfKeys() {
        addLine("zebra", "crossing");
        addLine("anteater", "nosey");

        var args = new SpcArgs();
        args.setSort(SpcArgs.SortMode.sorted);

        Fixer.fix(file, args);

        assertThat(file.getSettings().stream().map(Setting::getFullPath)).containsExactly("anteater", "zebra");
    }

    @Test
    void whenSortThenNumericOrderIsCorrect() {
        addLine("zebra9", "crossing");
        addLine("zebra1", "crossing");
        addLine("zebra100", "crossing");
        addLine("zebra52", "crossing");
        addLine("anteater", "nosey");

        var args = new SpcArgs();
        args.setSort(SpcArgs.SortMode.sorted);

        Fixer.fix(file, args);

        assertThat(file.getSettings().stream().map(Setting::getFullPath))
                .containsExactly("anteater", "zebra1", "zebra9", "zebra52", "zebra100");
    }

    @Test
    void whenSortedThenPathsAreInOrder() {
        addLine("path.to.property1", "scooby");
        addLine("otherPath.to.thefuture", "scooby");
        addLine("path.to.property9", "scooby");
        addLine("otherPath.to.property", "scooby");

        var args = new SpcArgs();
        args.setSort(SpcArgs.SortMode.sorted);

        Fixer.fix(file, args);

        assertThat(file.getSettings().stream().map(Setting::getFullPath))
                .containsExactly(
                        "otherPath.to.property", "otherPath.to.thefuture", "path.to.property1", "path.to.property9");
    }

    @Test
    void whenClusteredThenPathsAreMoved() {
        addLine("path.to.property1", "scooby");
        addLine("otherPath.to.thefuture", "scooby");
        addLine("path.to.property9", "scooby");
        addLine("otherPath.to.property", "scooby");

        var args = new SpcArgs();
        args.setSort(SpcArgs.SortMode.clustered);

        Fixer.fix(file, args);

        assertThat(file.getSettings().stream().map(Setting::getFullPath))
                .containsExactly(
                        "path.to.property1", "path.to.property9", "otherPath.to.thefuture", "otherPath.to.property");
    }

    @Test
    void whenClusteredWithMixedLengthsThenPathsAreMoved() {
        addLine("server.port", "8080");
        addLine("path.to.property1", "scooby");
        addLine("otherPath.to.thefuture", "scooby");
        addLine("path.to.property9", "scooby");
        addLine("otherPath.to.property", "scooby");
        addLine("server.skip", "false");
        addLine("a.b.c.d.e", "abcde");
        addLine("a.b.c.d", "abcd");

        var args = new SpcArgs();
        args.setSort(SpcArgs.SortMode.clustered);

        Fixer.fix(file, args);

        assertThat(file.getSettings().stream().map(Setting::getFullPath))
                .containsExactly(
                        "server.port",
                        "server.skip",
                        "path.to.property1",
                        "path.to.property9",
                        "otherPath.to.thefuture",
                        "otherPath.to.property",
                        "a.b.c.d.e",
                        "a.b.c.d");
    }

    @Test
    void whenConfiguredForCommonACommonFileCanAppear() throws Exception {
        PropertiesFile file1 = new PropertiesFile(new File("application-dev.properties"));
        PropertiesFile file2 = new PropertiesFile(new File("application-prod.properties"));

        SpcArgs args = new SpcArgs();
        args.setCommonProperties(SpcArgs.CommonPropertiesMode.multiple);
        Fixer.fix(List.of(file1, file2), args, writer);

        // then a properties file for common should have appeared
        then(writer).should().writeAll(argThat(list -> list.size() == 3), any(), any(), anyBoolean());
    }

    private void addLine(String key, String value) {
        file.add(new Setting(lineNumber++, List.of(), key, value));
    }
}
