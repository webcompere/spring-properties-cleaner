package uk.org.webcompere.spc.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import uk.org.webcompere.spc.cli.SpcArgs;

/**
 * Spring properties cleaner plugin
 */
@Mojo(name = "fix", defaultPhase = LifecyclePhase.COMPILE)
public class SpringPropertiesCleanerFixMojo extends SpringPropertiesCleanerMojoBase {

    public SpringPropertiesCleanerFixMojo() {
        super(SpcArgs.Action.fix);
    }

    public SpringPropertiesCleanerFixMojo(MavenProject project, ProcessorFactory factory) {
        super(project, SpcArgs.Action.fix, factory);
    }
}
