/*
 * Copyright (c) 2011 Caltha - Krzewski, Mach, Potempski Sp. J.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Caltha - Krzewski, Mach, Potempski Sp. J.
 */
package org.objectledge.maven.connectors.jsc;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.markers.MavenProblemInfo;
import org.eclipse.m2e.core.internal.markers.SourceLocation;
import org.eclipse.m2e.core.internal.markers.SourceLocationHelper;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

public class JscProjectConfigurator extends AbstractJavaProjectConfigurator {

	public static String MARKER_ID = "org.objectledge.maven.connectors.jsc.";

	public static String MARKER_SOURCE_DIR_MISSING = MARKER_ID
			+ "sourceDirMissing";

	@Override
	public AbstractBuildParticipant getBuildParticipant(
			IMavenProjectFacade projectFacade, MojoExecution execution,
			IPluginExecutionMetadata executionMetadata) {
		return new JscBuildParticipant(execution);
	}

	@SuppressWarnings("restriction")
	@Override
	public void configureRawClasspath(ProjectConfigurationRequest request,
			IClasspathDescriptor classpath, IProgressMonitor monitor)
			throws CoreException {

		IMavenProjectFacade facade = request.getMavenProjectFacade();
		IFile pom = facade.getPom();

		assertHasNature(request.getProject(), JavaCore.NATURE_ID);

		markerManager.deleteMarkers(pom, MARKER_ID);

		for (MojoExecution mojoExecution : getMojoExecutions(request, monitor)) {
			// TODO check if all plugin dependencies are available
			
			File sourceDirectory = getParameterValue("sourceDirectory",
					File.class, request.getMavenSession(), mojoExecution);
			if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
				SourceLocation location = SourceLocationHelper.findLocation(
						facade.getMavenProject(), new MojoExecutionKey(
								mojoExecution));
				markerManager
						.addErrorMarker(pom, MARKER_SOURCE_DIR_MISSING,
								new MavenProblemInfo("sourceDirectory "
										+ sourceDirectory + " does not exist",
										location));

			}
		}

		for (MojoExecution mojoExecution : getMojoExecutions(request, monitor)) {

			File outputDirectory = getParameterValue(
					getOutputFolderParameterName(), File.class,
					request.getMavenSession(), mojoExecution);

			File alternateOutputDirectory = new File(
					outputDirectory.getParentFile(), "js-"
							+ outputDirectory.getName());

			if (!alternateOutputDirectory.exists()) {
				if (!alternateOutputDirectory.mkdirs()) {
					String message = "failed to create directory "
							+ alternateOutputDirectory.getAbsolutePath();
					throw new CoreException(new Status(IStatus.ERROR,
							"org.objectledge.maven.connectors.jsc", message));
				}
			}

			IPath outputPath = getFullPath(facade, alternateOutputDirectory);

			if (!classpath.containsPath(outputPath)) {
				classpath.addLibraryEntry(outputPath).setExported(true);
			}
		}
	}
}
