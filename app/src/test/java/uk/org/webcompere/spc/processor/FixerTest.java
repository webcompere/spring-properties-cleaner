package uk.org.webcompere.spc.processor;

import org.junit.jupiter.api.Test;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class FixerTest {

    @Test
    void mergesDuplicates() {
        PropertiesFile file = new PropertiesFile(new File("foo"));
        file.add(new Setting(1, List.of(), "key", "value"));
        file.add(new Setting(2, List.of(), "key", "value"));

        Fixer.fix(file);

        assertThat(file.getSettings().size()).isEqualTo(1);

        assertThat(file.getSettings().get(0).getLine()).isEqualTo(2);
        assertThat(file.getSettings().get(0).getFullPath()).isEqualTo("key");
        assertThat(file.getSettings().get(0).getValue()).isEqualTo("value");
    }

    @Test
    void putsErrorLinesAtTheEndOfTheErrorsFooter() {
        PropertiesFile file = new PropertiesFile(new File("foo"));

        file.addError(1, "broken");
        file.addError(2, "also broken");
        file.addTrailingComments(List.of("# foo", "# bar"));

        Fixer.fix(file);

        assertThat(file.getLineErrors()).isEmpty();
        assertThat(file.getTrailingComments()).containsExactly(
                "# foo",
                "# bar",
                "# 1: broken",
                "# 2: also broken"
        );
    }
}