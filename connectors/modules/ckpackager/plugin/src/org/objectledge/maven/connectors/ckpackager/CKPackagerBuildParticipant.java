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
package org.objectledge.maven.connectors.ckpackager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.objectledge.ckpackager.PackScriptParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;

public class CKPackagerBuildParticipant extends MojoExecutionBuildParticipant {

	private static final Logger log = LoggerFactory
			.getLogger(CKPackagerBuildParticipant.class);

	private final PackScriptParser parser = new PackScriptParser();

	public CKPackagerBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {

		IMaven maven = MavenPlugin.getMaven();
		BuildContext buildContext = getBuildContext();

		File script = maven.getMojoParameterValue(getSession(),
				getMojoExecution(), "script", File.class);

		File outputDirectory = maven.getMojoParameterValue(getSession(),
				getMojoExecution(), "outputDirectory", File.class);

		if (script.exists() && script.isFile() && script.canRead()) {
			log.info("loading packaging script");
			List<File> sourceFiles = new ArrayList<File>();
			List<File> outputFiles = new ArrayList<File>();
			parser.parseScript(script, outputDirectory, sourceFiles,
					outputFiles);

			if (kind == IncrementalProjectBuilder.AUTO_BUILD
					|| kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) {
				log.info("incremental build, checking sources for modifications");
				// if pack script changed we'll rebuild
				if (!buildContext.hasDelta(script)) {
					// pack script hasn't changed, check source files
					boolean sourcesChanged = false;
					for (File sourceFile : sourceFiles) {
						if (buildContext.hasDelta(sourceFile)) {
							sourcesChanged = true;
						}
					}
					if (!sourcesChanged) {
						log.info("nothing changed, skipping ckpackager plugin");
						return null;
					}
				}
				log.info("there were source changes, running ckpackager plugin");
			} else {
				log.info("full build, running ckpackager plugin");
			}

			// execute mojo
			Set<IProject> result = super.build(kind, monitor);

			// refresh output files
			log.info("refreshing output files");
			for (File outputFile : outputFiles) {
				buildContext.refresh(outputFile);
			}

			return result;
		} else {
			throw new MojoFailureException("packaging script " + script
					+ " does not exist or is unreadable");
		}

	}
}
