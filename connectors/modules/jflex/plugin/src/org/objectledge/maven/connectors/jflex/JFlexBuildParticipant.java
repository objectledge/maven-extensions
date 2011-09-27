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
package org.objectledge.maven.connectors.jflex;

import java.io.File;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class JFlexBuildParticipant extends MojoExecutionBuildParticipant {

	private static final String[] JFLEX_INCLUDES = new String[] { "**/*.lex",
			"**/*.flex", "**/*.jlex", "**/*.jflex" };

	public JFlexBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {
		IMaven maven = MavenPlugin.getMaven();
		BuildContext buildContext = getBuildContext();

		// check if any of the grammar files changed
		if (!sourcesChanged(buildContext,
				sourceLocations(maven, getSession(), getMojoExecution()))) {
			return null;
		}

		// execute mojo
		Set<IProject> result = super.build(kind, monitor);

		// tell m2e builder to refresh generated files
		File generated = maven.getMojoParameterValue(getSession(),
				getMojoExecution(), "outputDirectory", File.class);
		if (generated != null) {
			buildContext.refresh(generated);
		}

		return result;
	}

	private File[] sourceLocations(IMaven maven, MavenSession session,
			MojoExecution execution) throws Exception {
		/*
		 * List of grammar definitions to run the JFlex parser generator on.
		 * Each path may either specify a single grammar file or a directory.
		 * Directories will be recursively scanned for files with one of the
		 * following extensions: ".jflex", ".flex", ".jlex" or ".lex". By
		 * default, all files in src/main/jflex will be processed.
		 */
		File[] lexDefinitions = maven.getMojoParameterValue(getSession(),
				getMojoExecution(), "lexDefinitions", File[].class);
		if (lexDefinitions == null || lexDefinitions.length == 0) {
			return new File[] { new File(session.getCurrentProject()
					.getBasedir(), "src/main/jflex") };
		}
		return lexDefinitions;
	}

	private static boolean sourcesChanged(BuildContext buildContext,
			File[] sourceLocations) {
		for (File sourceLocation : sourceLocations) {
			if (sourceLocation.isFile()) {
				if (buildContext.hasDelta(sourceLocation)) {
					return true;
				}
			} else {
				// delta or full scanner
				Scanner ds = buildContext.newScanner(sourceLocation);
				ds.setIncludes(JFLEX_INCLUDES);
				ds.scan();
				String[] includedFiles = ds.getIncludedFiles();
				if (includedFiles != null && includedFiles.length > 0) {
					return true;
				}
			}
		}
		return false;
	}
}
