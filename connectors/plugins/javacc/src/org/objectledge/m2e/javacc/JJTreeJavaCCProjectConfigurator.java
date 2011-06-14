package org.objectledge.m2e.javacc;

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;

public class JJTreeJavaCCProjectConfigurator extends
		AbstractJavaProjectConfigurator {
	@Override
	public AbstractBuildParticipant getBuildParticipant(
			IMavenProjectFacade projectFacade, MojoExecution execution,
			IPluginExecutionMetadata executionMetadata) {
		return new JavaCCBuildParticipant(execution);
	}

	@Override
	protected File[] getSourceFolders(ProjectConfigurationRequest request,
			MojoExecution mojoExecution) throws CoreException {

		File generated = maven.getMojoParameterValue(request.getMavenSession(),
				mojoExecution, "outputDirectory", File.class);

		File interim = maven.getMojoParameterValue(request.getMavenSession(),
				mojoExecution, "interimDirectory", File.class);

		return new File[] { interim, generated };
	}
}
