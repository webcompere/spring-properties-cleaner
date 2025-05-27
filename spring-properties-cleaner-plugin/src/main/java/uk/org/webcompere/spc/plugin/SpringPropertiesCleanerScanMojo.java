package uk.org.webcompere.spc.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import uk.org.webcompere.spc.cli.SpcArgs;

/**
 * Spring properties cleaner plugin
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.COMPILE)
public class SpringPropertiesCleanerScanMojo extends SpringPropertiesCleanerMojoBase {
    public SpringPropertiesCleanerScanMojo() {
        super(SpcArgs.Action.scan);
    }

    public SpringPropertiesCleanerScanMojo(MavenProject project, ProcessorFactory factory) {
        super(project, SpcArgs.Action.scan, factory);
    }
}
