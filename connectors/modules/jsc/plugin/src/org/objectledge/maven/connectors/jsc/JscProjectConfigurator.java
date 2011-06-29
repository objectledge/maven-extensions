package org.objectledge.maven.connectors.jsc;

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class JscProjectConfigurator extends AbstractJavaProjectConfigurator {
	@Override
	public AbstractBuildParticipant getBuildParticipant(
			IMavenProjectFacade projectFacade, MojoExecution execution,
			IPluginExecutionMetadata executionMetadata) {
		return new JscBuildParticipant(execution);
	}

	@Override
	public void configureRawClasspath(ProjectConfigurationRequest request,
			IClasspathDescriptor classpath, IProgressMonitor monitor)
			throws CoreException {

		IMavenProjectFacade facade = request.getMavenProjectFacade();

		assertHasNature(request.getProject(), JavaCore.NATURE_ID);

		for (MojoExecution mojoExecution : getMojoExecutions(request, monitor)) {

			File outputDirectory = getParameterValue(
					getOutputFolderParameterName(), File.class,
					request.getMavenSession(), mojoExecution);

			File alternateOutputDirectory = new File(
					outputDirectory.getParentFile(), "js-"
							+ outputDirectory.getName());

			IPath outputPath = getFullPath(facade, alternateOutputDirectory);

			if (!classpath.containsPath(outputPath)) {
				classpath.addLibraryEntry(outputPath).setExported(true);
			}
		}
	}
}
