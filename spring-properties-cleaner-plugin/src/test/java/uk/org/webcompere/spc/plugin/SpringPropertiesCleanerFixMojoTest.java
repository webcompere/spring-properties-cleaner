package uk.org.webcompere.spc.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@ExtendWith(SystemStubsExtension.class)
class SpringPropertiesCleanerFixMojoTest {

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @Test
    void projectWithNoResourcesIsError() {
        MavenProject mavenProject = new MavenProject();
        SpringPropertiesCleanerFixMojo mojo = new SpringPropertiesCleanerFixMojo(mavenProject);

        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class);
    }
}
