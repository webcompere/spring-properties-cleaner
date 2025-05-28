package uk.org.webcompere.spc.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.LineError;
import uk.org.webcompere.spc.model.PropertiesFile;

class ParserTest {
    private PropertiesFile file = new PropertiesFile(new File("foo"));

    private Parser parser = new Parser(file);

    @Test
    void givenNewFileAndNoLinesThenFileIsEmpty() {
        assertThat(file.getSettings()).isEmpty();
        assertThat(file.getTrailingComments()).isEmpty();
    }

    @Test
    void whenAddACommentOnlyThenGoesIntoTrailingLines() {
        parser.parse("# this is a comment");
        parser.close();

        assertThat(file.getTrailingComments()).containsExactly("# this is a comment");
    }

    @Test
    void whenAddCommentsSeparatedBySpacesThenGoIntoTrailing() {
        parser.parse("# this is a comment");
        parser.parse("");
        parser.parse("# this is another comment");
        parser.close();

        assertThat(file.getTrailingComments()).containsExactly("# this is a comment", "", "# this is another comment");
    }

    @Test
    void whenAddBlankThenIsComment() {
        parser.parse("     ");
        parser.close();

        assertThat(file.getTrailingComments()).containsExactly("     ");
    }

    @Test
    void whenAddErrorThenIsError() {
        parser.parse("oops I did it again");

        assertThat(file.getLineErrors()).containsExactly(new LineError(1, "oops I did it again"));
    }

    @Test
    void whenAddASettingThenIsSetting() {
        parser.parse("britney=spears");

        assertThat(file.getSettings().get(0).getFullPath()).isEqualTo("britney");
        assertThat(file.getSettings().get(0).getValue()).isEqualTo("spears");
    }

    @Test
    void whenAddASettingWithSpacingThenIsTrimmed() {
        parser.parse("sabrina = carpenter");

        assertThat(file.getSettings().get(0).getFullPath()).isEqualTo("sabrina");
        assertThat(file.getSettings().get(0).getValue()).isEqualTo("carpenter");
    }

    @Test
    void whenAddASettingWithInterpolationThenInterpolationSurvives() {
        parser.parse("sabrina=${carpenter}");

        assertThat(file.getSettings().get(0).getFullPath()).isEqualTo("sabrina");
        assertThat(file.getSettings().get(0).getValue()).isEqualTo("${carpenter}");
    }

    @Test
    void whenAddASettingWithCommentBeforeThenCommentInsideSetting() {
        parser.parse("# The Spearsmo");
        parser.parse("britney=spears");

        assertThat(file.getSettings().get(0).getPrecedingComments()).containsExactly("# The Spearsmo");
    }

    @Test
    void whenAddASettingWithBlankLineAndCommentBeforeThenBlankLineAndCommentInsideSetting() {
        parser.parse("");
        parser.parse("# The Spearsmo");
        parser.parse("britney=spears");

        assertThat(file.getSettings().get(0).getPrecedingComments()).containsExactly("", "# The Spearsmo");

        assertThat(file.toLines(SpcArgs.WhiteSpaceMode.preserve))
                .containsExactly("", "# The Spearsmo", "britney=spears");
    }

    @Test
    void whenAddASettingWithCommentBeforeThenLineNumberOfPropertyIsWhereThePropertyIsInFile() {
        parser.parse("# The Spearsmo");
        parser.parse("britney=spears");

        assertThat(file.getSettings().get(0).getLine()).isEqualTo(2);
    }

    @Test
    void whenAddMultipleSettingsWithCommentBeforeThenCommentInsideSetting() {
        parser.parse("# The Spearsmo");
        parser.parse("britney=spears");
        parser.parse("# The Aguilerio");
        parser.parse("christina=agorilla");

        assertThat(file.getSettings().get(0).getPrecedingComments()).containsExactly("# The Spearsmo");
        assertThat(file.getSettings().get(1).getPrecedingComments()).containsExactly("# The Aguilerio");
    }
}
