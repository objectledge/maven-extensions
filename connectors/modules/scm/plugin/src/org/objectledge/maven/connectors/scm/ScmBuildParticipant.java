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
package org.objectledge.maven.connectors.scm;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class ScmBuildParticipant extends MojoExecutionBuildParticipant {

	public ScmBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {

		// execute mojo
		Set<IProject> result = super.build(kind, monitor);

		IMaven maven = MavenPlugin.getMaven();
		BuildContext buildContext = getBuildContext();
		MojoExecution mojoExecution = getMojoExecution();
		
		String targetDirectoryParameter = "workingDirectory";
		if ("bootstrap".equals(mojoExecution.getGoal())) {
			targetDirectoryParameter = "checkoutDirectory";
		}
		if ("checkout".equals(mojoExecution.getGoal())) {
			targetDirectoryParameter = "checkoutDirectory";
		} 
		File targetDirectory = maven.getMojoParameterValue(getSession(),
				getMojoExecution(), targetDirectoryParameter, File.class);
		if (targetDirectory != null) {
			buildContext.refresh(targetDirectory);
		}

		return result;
	}
}
