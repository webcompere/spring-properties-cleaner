package uk.org.webcompere.spc.plugin;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@ExtendWith(SystemStubsExtension.class)
class SpringPropertiesCleanerScanMojoTest {

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @Test
    void projectWithNoResourcesIsError() {
        MavenProject mavenProject = new MavenProject();
        SpringPropertiesCleanerScanMojo mojo = new SpringPropertiesCleanerScanMojo(mavenProject);

        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class);
    }

    @Test
    void canExecuteScan() {
        MavenProject mavenProject = new MavenProject();
        Resource resource = new Resource();
        resource.setDirectory(Paths.get("src", "test", "resources").toString());
        mavenProject.getResources().add(resource);
        SpringPropertiesCleanerScanMojo mojo = new SpringPropertiesCleanerScanMojo(mavenProject);
        mojo.prefix = "example";

        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoFailureException.class);

        assertThat(systemErr.getLines())
                .contains("[error] example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }

    @Test
    void canExecuteScanAgainstReadParameter() {
        MavenProject mavenProject = new MavenProject();
        SpringPropertiesCleanerScanMojo mojo = new SpringPropertiesCleanerScanMojo(mavenProject);
        mojo.read = Paths.get("src", "test", "resources").toString();
        mojo.prefix = "example";

        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoFailureException.class);

        assertThat(systemErr.getLines())
                .contains("[error] example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }
}