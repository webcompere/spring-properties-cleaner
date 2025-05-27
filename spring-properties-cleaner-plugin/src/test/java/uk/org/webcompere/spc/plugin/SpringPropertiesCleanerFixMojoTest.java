package uk.org.webcompere.spc.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.webcompere.spc.processor.Processor;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

@ExtendWith(SystemStubsExtension.class)
@ExtendWith(MockitoExtension.class)
class SpringPropertiesCleanerFixMojoTest {

    @Mock
    private Processor processor;

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @Test
    void defaultConstructor() {
        assertThat(new SpringPropertiesCleanerFixMojo()).isNotNull();
    }

    @Test
    void projectWithNoResourcesIsError() {
        MavenProject mavenProject = new MavenProject();
        SpringPropertiesCleanerFixMojo mojo = new SpringPropertiesCleanerFixMojo(mavenProject, (a, b) -> processor);

        assertThatThrownBy(mojo::execute).isInstanceOf(MojoExecutionException.class);
    }
}
