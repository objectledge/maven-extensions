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
import java.util.Set;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.markers.MavenProblemInfo;
import org.eclipse.m2e.core.internal.markers.SourceLocation;
import org.eclipse.m2e.core.internal.markers.SourceLocationHelper;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class JscBuildParticipant extends MojoExecutionBuildParticipant {

	public JscBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {
		IMaven maven = MavenPlugin.getMaven();
		BuildContext buildContext = getBuildContext();
		MojoExecution mojoExecution = getMojoExecution();

		File source = maven.getMojoParameterValue(getSession(),
				mojoExecution, "sourceDirectory", File.class);
		if(!source.exists() || !source.isDirectory()) {
			// skip non-existent source directories
			return null;
		}
		
		// check if any of the input files changed
		Scanner ds = buildContext.newScanner(source); // delta or full scanner
		ds.scan();
		String[] includedFiles = ds.getIncludedFiles();
		if (includedFiles == null || includedFiles.length <= 0) {
			return null;
		}

		// override outputDirectory
		File outputDirectory = maven.getMojoParameterValue(getSession(),
				mojoExecution, "outputDirectory", File.class);
		File alternateOutputDirectory = new File(
				outputDirectory.getParentFile(), "js-"
						+ outputDirectory.getName());

		Xpp3Dom dom = mojoExecution.getConfiguration();
		Xpp3Dom outputDirectoryConfig = dom.getChild("outputDirectory");
		outputDirectoryConfig.setValue(alternateOutputDirectory
				.getAbsolutePath());

		// execute mojo
		Set<IProject> result = super.build(kind, monitor);

		// tell m2e builder to refresh generated files
		buildContext.refresh(alternateOutputDirectory);

		return result;
	}
}
