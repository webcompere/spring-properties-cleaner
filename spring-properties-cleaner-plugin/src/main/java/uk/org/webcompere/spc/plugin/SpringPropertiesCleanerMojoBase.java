package uk.org.webcompere.spc.plugin;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.processor.Processor;

import java.util.Optional;

public abstract class SpringPropertiesCleanerMojoBase extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter
    boolean identicalDuplicatesAreErrors = true;

    @Parameter
    String read;

    @Parameter
    SpcArgs.SortMode sort;

    @Parameter
    SpcArgs.CommonPropertiesMode common;

    @Parameter
    boolean apply = true;

    @Parameter
    String prefix;

    private SpcArgs.Action action;

    public SpringPropertiesCleanerMojoBase(SpcArgs.Action action) {
        this.action = action;
    }

    public SpringPropertiesCleanerMojoBase(MavenProject project, SpcArgs.Action action) {
        this.project = project;
        this.action = action;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        SpcArgs arguments = new SpcArgs();

        String resourceDirectory = Optional.ofNullable(read).orElseGet(this::findDefaultScanDirectory);
        getLog().info("Executing scan on " + resourceDirectory);

        arguments.setRead(resourceDirectory);
        arguments.setIdenticalDuplicatesAreErrors(identicalDuplicatesAreErrors);
        arguments.setAction(action);

        if (sort != null) {
            arguments.setSort(sort);
        }
        if (common != null) {
            arguments.setCommonProperties(common);
        }
        arguments.setApply(apply);

        if (prefix != null) {
            arguments.setPrefix(prefix);
        }

        var processor = new Processor(getLog()::error, getLog()::info);
        try {
            if (!processor.execute(arguments)) {
                throw new MojoFailureException("Errors detected in properties files in " + resourceDirectory);
            }
        } catch (AbstractMojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Execution failed", e);
        }
    }

    private String findDefaultScanDirectory() {
        if (project.getResources().isEmpty()) {
            getLog().error("No resources directory found");
            throw new RuntimeException("No resources directory found");
        }

        return ((Resource)project.getResources().get(0)).getDirectory();
    }
}
