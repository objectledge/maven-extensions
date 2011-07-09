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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

public class ScmBuildParticipant extends MojoExecutionBuildParticipant {

	public ScmBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {

		IMaven maven = MavenPlugin.getMaven();

		File targetDirectory = getOutputDirectory(maven, getSession(),
				getMojoExecution());
		String oldLayout = directoryLayout(targetDirectory);

		// execute mojo
		Set<IProject> result = super.build(kind, monitor);

		// check if directory layout changed
		String newLayout = directoryLayout(targetDirectory);
		if (!newLayout.equals(oldLayout)) {
			// immediate refresh, because checkout results need to be visible to
			// project configurators
			refreshResource(getMavenProjectFacade().getProject(),
					targetDirectory, monitor);
			IProjectConfigurationManager configManager = MavenPlugin
					.getProjectConfigurationManager();
			configManager.updateProjectConfiguration(getMavenProjectFacade()
					.getProject(), monitor);
		} else {
			// lazy refersh
			getBuildContext().refresh(targetDirectory);
		}

		return result;
	}

	/**
	 * Get output directory of the SCM plugin.
	 */
	private static File getOutputDirectory(IMaven maven, MavenSession session,
			MojoExecution mojoExecution) throws CoreException {
		String targetDirectoryParameter;
		if ("bootstrap".equals(mojoExecution.getGoal())
				|| "checkout".equals(mojoExecution.getGoal())) {
			targetDirectoryParameter = "checkoutDirectory";
		} else {
			targetDirectoryParameter = "workingDirectory";
		}
		File targetDirectory = maven.getMojoParameterValue(session,
				mojoExecution, targetDirectoryParameter, File.class);
		return targetDirectory;
	}

	/**
	 * Record directory layout as a string composed of newline-separated
	 * absolute paths of directories inside given directory, or empty string if
	 * the top level directory does not exist.
	 * 
	 * @param dir
	 *            top level directory.
	 * @return string describing directory layout.
	 */
	private static String directoryLayout(File dir) {
		StringBuilder buff = new StringBuilder();
		if (dir.exists()) {
			Queue<File> stack = new LinkedList<File>();
			stack.add(dir);
			while (!stack.isEmpty()) {
				File cur = stack.remove();
				buff.append(cur.getAbsolutePath()).append('\n');
				File[] children = cur.listFiles();
				for (File child : children) {
					if (child.isDirectory()) {
						stack.add(child);
					}
				}
			}
			return buff.toString();
		} else {
			return "";
		}
	}

	/**
	 * Refresh workspace starting with given path.
	 * 
	 * @param project
	 *            project.
	 * @param file
	 *            refresh origin.
	 */
	private static void refreshResource(IProject project, File file,
			IProgressMonitor monitor) throws CoreException {
		if (project == null || file == null) {
			return;
		}

		IPath projectPath = project.getLocation();
		if (projectPath == null) {
			return;
		}

		IPath path = new Path(file.getAbsolutePath());
		if (!projectPath.isPrefixOf(path)) {
			return;
		}

		path = path.removeFirstSegments(projectPath.segmentCount());

		if (!file.exists()) {
			IResource resource = project.findMember(path);
			if (resource != null) {
				resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		} else if (file.isDirectory()) {
			IFolder ifolder = project.getFolder(path);
			ifolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} else {
			IFile ifile = project.getFile(path);
			ifile.refreshLocal(IResource.DEPTH_ZERO, monitor);
		}
	}
}
