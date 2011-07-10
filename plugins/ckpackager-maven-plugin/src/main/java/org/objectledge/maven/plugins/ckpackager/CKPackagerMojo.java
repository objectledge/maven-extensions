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
package org.objectledge.maven.plugins.ckpackager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.objectledge.ckpackager.Launcher;
import org.objectledge.ckpackager.PackScriptParser;

/**
 * Packages custom CKEditor distribution.
 * 
 * @see http://docs.cksource.com/CKEditor_3.x/Developers_Guide/CKPackager
 * 
 * @phase generate-resources
 * @goal package
 */
public class CKPackagerMojo extends AbstractMojo {

	/**
	 * Location of ckpackager configuration file.
	 * 
	 * @parameter 
	 *            default-value="${project.basedir}/src/main/resources/ckeditor.pack"
	 * @required
	 */
	private File script;

	/**
	 * Location of ckpackager output directory.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}/generated-resources/ckpackager"
	 */
	private File outputDirectory;

	/**
	 * PackScriptParser instance.
	 */
	private final PackScriptParser parser = new PackScriptParser();

	public void execute() throws MojoExecutionException, MojoFailureException {

		if (script.exists() && script.isFile() && script.canRead()) {
			getLog().info(
					"Processing packaging script " + script.getAbsolutePath());

			List<File> sourceFiles = new ArrayList<File>();
			List<File> outputFiles = new ArrayList<File>();

			try {
				parser.parseScript(script, outputDirectory, sourceFiles,
						outputFiles);
			} catch (Exception e) {
				throw new MojoExecutionException(
						"packaging script parsing failed", e);
			}

			for (File outputFile : outputFiles) {
				File outputDir = outputFile.getParentFile();
				if (!outputDir.exists()) {
					if (!outputDir.mkdirs()) {
						throw new MojoExecutionException(
								"failed to create output directory "
										+ outputDir.getAbsolutePath());
					}
				}
			}

			try {
				Launcher.main(new String[] { script.getAbsolutePath(),
						"-output", outputDirectory.getAbsolutePath() });
			} catch (RuntimeException e) {
				throw new MojoExecutionException(
						"ckpackager invocation failed", e);
			}
		} else {
			throw new MojoFailureException(script.getAbsolutePath()
					+ " does not exist or is unreadable");
		}
	}
}
