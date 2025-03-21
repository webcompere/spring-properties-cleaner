package uk.org.webcompere.spc.parser;

import org.junit.jupiter.api.Test;
import uk.org.webcompere.spc.model.LineError;
import uk.org.webcompere.spc.model.PropertiesFile;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest {
    private PropertiesFile file = new PropertiesFile("foo");

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

        assertThat(file.getTrailingComments())
                .containsExactly("# this is a comment", "", "# this is another comment");
    }

    @Test
    void whenAddBlankThenIsComment() {
        parser.parse("     ");
        parser.close();

        assertThat(file.getTrailingComments())
                .containsExactly("     ");
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
    void whenAddASettingWithCommentBeforeThenCommentInsideSetting() {
        parser.parse("# The Spearsmo");
        parser.parse("britney=spears");

        assertThat(file.getSettings().get(0).getPrecedingComments()).containsExactly("# The Spearsmo");
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

    @Test
    void whenWhitespaceInCommentsOnlyCommentLinesIncluded() {
        parser.parse("    ");
        parser.parse("# The Spearsmo");
        parser.parse("    ");
        parser.parse("britney=spears");

        assertThat(file.getSettings().get(0).getPrecedingComments()).containsExactly("# The Spearsmo");
    }
}