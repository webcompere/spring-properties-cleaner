package uk.org.webcompere.spc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.security.AbortExecutionException;
import uk.org.webcompere.systemstubs.security.SystemExit;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

@ExtendWith(SystemStubsExtension.class)
class AppTest {

    @SystemStub
    private SystemExit exit;

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @Test
    void whenParametersNotProvidedThenExit() {
        assertThatThrownBy(() -> App.main(new String[0])).isInstanceOf(AbortExecutionException.class);

        assertThat(systemOut.getLines()).contains("Usage: Spring properties cleaner [options]");
        assertThat(exit.getExitCode()).isEqualTo(1);
    }

    @Test
    void whenBadReadDirectoryThenExit() {
        assertThatThrownBy(() -> App.main(new String[] {"--read", "/path/to/nowhere"}))
                .isInstanceOf(AbortExecutionException.class);

        assertThat(exit.getExitCode()).isEqualTo(1);
    }

    @Test
    void whenHelpRequestedNoExit() {
        App.main(new String[] {"--help"});

        assertThat(systemOut.getLines()).contains("Usage: Spring properties cleaner [options]");
    }

    @Test
    void readingDuplicatesIsError() {
        assertThatThrownBy(() -> App.main(
                        new String[] {"--read", "src/test/resources/example-with-duplicate-identical.properties"}))
                .isInstanceOf(AbortExecutionException.class);

        assertThat(systemErr.getLines())
                .contains(
                        "example-with-duplicate-identical.properties: property1 has duplicate values L2:'foo',L7:'foo'");
    }

    @Test
    void readingGoodFileIsNotError() {
        App.main(new String[] {"--read", "src/test/resources/example-with-no-duplicate.properties"});

        assertThat(systemOut.getLines()).containsExactly("");
    }
}
