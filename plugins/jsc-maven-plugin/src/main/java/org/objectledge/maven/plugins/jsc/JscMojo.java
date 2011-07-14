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
package org.objectledge.maven.plugins.jsc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.tools.jsc.Main;

/**
 * A goal that invokes Rhino jsc compiler.
 * 
 * Based on http://cefiro.homelinux.org/topics/java/maven-rhino-compiler-plugin/
 * 
 * @goal compile
 * @phase process-sources
 * 
 * @author rkrzewski <rafal.krzewski@objectledge.org>
 */
public class JscMojo extends AbstractMojo {

	/**
	 * Compiler output directory.
	 * 
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * 
	 * JavaScript source directory.
	 * 
	 * @parameter default-value="${basedir}/src/main/js"
	 * @required
	 */

	private File sourceDirectory;

	/**
	 * JavaScript sources file name extension.
	 * 
	 * @parameter default-value="js"
	 * @required
	 */
	private String sourceExtension;

	/**
	 * Java package name of the generated classes.
	 * 
	 * @parameter default-value=""
	 */
	private String packageName;

	/**
	 * Superclass of the generated classes.
	 * 
	 * @parameter default-value=""
	 */
	private String superClassName;

	/**
	 * Comma separated list of interfaces to be implemented by the generated classes.
	 * 
	 * @parameter default-value=""
	 */
	private String interfaceNames;

	/**
	 * Specifies that debug information should be generated. May not be combined
	 * with optimization at an optLevel greater than zero.
	 * 
	 * @parameter default-value="false";
	 */
	private boolean debug;

	/**
	 * Optimization level, must be an integer between -1 and 9. If optLevel is
	 * greater than zero, debug option may not be specified.
	 * 
	 * @parameter default-value="0";
	 */
	private int optLevel;

	/**
	 * JavaScript Language version number, allowed values are 100, 110, 120, 130, 140, 150, 160.
	 * 
	 * @parameter default-value="160"
	 * @required
	 */
	private String versionNumber;

	public void execute() throws MojoExecutionException, MojoFailureException {
		Log log = getLog();
		log.debug(String.format("sourceDirectory : %s\n", sourceDirectory));
		log.debug(String.format("sourceExtension : %s\n", sourceExtension));
		log.debug(String.format("outputDirectory : %s\n", outputDirectory));
		log.debug(String.format("packageName     : %s\n", packageName));
		log.debug(String.format("versionNumber   : %s\n", versionNumber));

		File f = outputDirectory;
		if (!f.exists()) {
			f.mkdirs();
		}

		List<String> argList = new ArrayList<String>();
		appendArguments(argList);
		appendSources(argList);

		Main.main(argList.toArray(new String[argList.size()]));
	}

	private void appendArguments(List<String> argList) {

		if (packageName != null && packageName.length() > 0) {
			argList.add("-package");
			argList.add(packageName);
		}

		if (superClassName != null && superClassName.length() > 0) {
			argList.add("-extends");
			argList.add(superClassName);
		}

		if (interfaceNames != null && interfaceNames.length() > 0) {
			argList.add("-implements");
			argList.add(interfaceNames);
		}

		if (debug) {
			argList.add("-debug");
		}

		if (optLevel != 0) {
			argList.add("-opt");
			argList.add(Integer.toString(optLevel));
		}

		if (versionNumber != null) {
			argList.add("-version");
			argList.add(versionNumber);
		}

		if (outputDirectory != null) {
			argList.add("-d");
			argList.add(outputDirectory.getAbsolutePath());
		}
	}

	void appendSources(List<String> argList) {
		if (sourceDirectory.isDirectory()) {
			String[] extensions = sourceExtension.split(",");
			@SuppressWarnings("unchecked")
			Collection<File> files = FileUtils.listFiles(sourceDirectory,
					extensions, true);
			if (files != null) {
				for (File file : files) {
					argList.add(file.toString());
				}
			}
		}
	}
}
