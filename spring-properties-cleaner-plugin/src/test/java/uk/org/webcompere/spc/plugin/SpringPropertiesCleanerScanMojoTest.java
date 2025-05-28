package uk.org.webcompere.spc.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

import java.nio.file.Paths;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.processor.Processor;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;

@ExtendWith(SystemStubsExtension.class)
@ExtendWith(MockitoExtension.class)
class SpringPropertiesCleanerScanMojoTest {

    @Mock
    private Processor processor;

    @Captor
    private ArgumentCaptor<SpcArgs> captor;

    @SystemStub
    private SystemOut systemOut = new SystemOut(tapAndOutput());

    @SystemStub
    private SystemErr systemErr = new SystemErr(tapAndOutput());

    @Test
    void defaultConstructor() {
        assertThat(new SpringPropertiesCleanerScanMojo()).isNotNull();
    }

    @Test
    void projectWithNoResourcesIsError() {
        MavenProject mavenProject = new MavenProject();
        SpringPropertiesCleanerScanMojo mojo = new SpringPropertiesCleanerScanMojo(mavenProject, (a, b) -> processor);

        assertThatThrownBy(mojo::execute).isInstanceOf(MojoExecutionException.class);
    }

    @Test
    void canExecuteScan() {
        SpringPropertiesCleanerScanMojo mojo = new SpringPropertiesCleanerScanMojo(validMavenProject(), Processor::new);
        mojo.prefix = "example";

        assertThatThrownBy(mojo::execute).isInstanceOf(MojoFailureException.class);

        assertThat(systemErr.getLines())
                .contains(
                        "[error] example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }

    @Test
    void canExecuteScanAgainstReadParameter() {
        MavenProject mavenProject = new MavenProject();
        SpringPropertiesCleanerScanMojo mojo = new SpringPropertiesCleanerScanMojo(mavenProject, Processor::new);
        mojo.read = Paths.get("src", "test", "resources").toString();
        mojo.prefix = "example";

        assertThatThrownBy(mojo::execute).isInstanceOf(MojoFailureException.class);

        assertThat(systemErr.getLines())
                .contains(
                        "[error] example-with-duplicate.properties: property1 has duplicate values L2:'foo',L7:'boo'");
    }

    @Test
    void sortModeIsPassedOn() throws Exception {
        given(processor.execute(any())).willReturn(true);

        SpringPropertiesCleanerScanMojo mojo =
                new SpringPropertiesCleanerScanMojo(validMavenProject(), (a, b) -> processor);
        mojo.sort = SpcArgs.SortMode.clustered;
        mojo.execute();

        then(processor).should().execute(captor.capture());

        assertThat(captor.getValue().getSort()).isEqualTo(SpcArgs.SortMode.clustered);
    }

    @Test
    void whitespaceModeIsPassedOn() throws Exception {
        given(processor.execute(any())).willReturn(true);

        SpringPropertiesCleanerScanMojo mojo =
                new SpringPropertiesCleanerScanMojo(validMavenProject(), (a, b) -> processor);
        mojo.whitespace = SpcArgs.WhiteSpaceMode.section;
        mojo.execute();

        then(processor).should().execute(captor.capture());

        assertThat(captor.getValue().getWhiteSpaceMode()).isEqualTo(SpcArgs.WhiteSpaceMode.section);
    }

    @Test
    void commonPropertiesModeIsPassedOn() throws Exception {
        given(processor.execute(any())).willReturn(true);

        SpringPropertiesCleanerScanMojo mojo =
                new SpringPropertiesCleanerScanMojo(validMavenProject(), (a, b) -> processor);
        mojo.common = SpcArgs.CommonPropertiesMode.multiple;
        mojo.execute();

        then(processor).should().execute(captor.capture());

        assertThat(captor.getValue().getCommonProperties()).isEqualTo(SpcArgs.CommonPropertiesMode.multiple);
    }

    @Test
    void applyIsFalse() throws Exception {
        given(processor.execute(any())).willReturn(true);

        SpringPropertiesCleanerScanMojo mojo =
                new SpringPropertiesCleanerScanMojo(validMavenProject(), (a, b) -> processor);
        mojo.execute();

        then(processor).should().execute(captor.capture());

        assertThat(captor.getValue().isApply()).isFalse();
    }

    private MavenProject validMavenProject() {
        MavenProject mavenProject = new MavenProject();
        Resource resource = new Resource();
        resource.setDirectory(Paths.get("src", "test", "resources").toString());
        mavenProject.getResources().add(resource);
        return mavenProject;
    }
}
