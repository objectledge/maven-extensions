package org.objectledge.maven.connectors.javacc;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;

/**
 * Common base class for JavaCC project configurator classes. 
 * 
 * @author rafal.krzewski@objectledge.org
 */
abstract class AbstractJavaCCProjectConfigurator extends
		AbstractJavaProjectConfigurator {

	abstract File[] getGeneratedSourceFolders(MavenSession session,
			MojoExecution execution) throws CoreException;

	@Override
	protected File[] getSourceFolders(
			ProjectConfigurationRequest configurationrequest,
			MojoExecution mojoExecution) throws CoreException {
		return getGeneratedSourceFolders(
				configurationrequest.getMavenSession(), mojoExecution);
	}
}
