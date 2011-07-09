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
package org.objectledge.maven.connectors.coral;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

@SuppressWarnings("restriction")
public class CoralGenerationTest extends AbstractMavenProjectTestCase {

	public void testInitialBuild() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
		IProject project1 = importProject("projects/testproject-01/pom.xml",
				configuration);
		waitForJobsToComplete();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		assertNoErrors(project1);

		IFile interfaceFile = project1
				.getFile("src/main/java/org/objectledge/maven/connectors/coral/tests/SampleResource.java");

		assertTrue("interface file not synchronized",
				interfaceFile.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue("interface file not accessible",
				interfaceFile.isAccessible());
	}

	public void testIncrementalBuild() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
		IProject project1 = importProject("projects/testproject-02/pom.xml",
				configuration);
		waitForJobsToComplete();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		assertNoErrors(project1);

		IFile interfaceFile = project1
				.getFile("src/main/java/org/objectledge/maven/connectors/coral/tests/SampleResource.java");

		assertTrue("interface file not synchronized",
				interfaceFile.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue("interface file not accessible",
				interfaceFile.isAccessible());

		IFile rmlFile = project1.getFile("src/main/resources/rml/sample.rml");
		String rmlContents = getContents(rmlFile);
		String interfaceContents = getContents(interfaceFile);
		assertFalse("interface file not clean",
				interfaceContents.contains("getSecondAttribute()"));

		rmlContents = rmlContents.replace("string firstProperty",
				"string firstProperty,\nstring secondAttribute");
		setContents(rmlFile, rmlContents);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		interfaceFile = project1
				.getFile("src/main/java/org/objectledge/maven/connectors/coral/tests/SampleResource.java");
		assertTrue("interface file not synchronized",
				interfaceFile.isSynchronized(IResource.DEPTH_ZERO));
		interfaceContents = getContents(interfaceFile);
		assertTrue("interface file does not reference secondAttribute",
				interfaceContents.contains("getSecondAttribute()"));
	}

	private static void setContents(IFile file, String contents) throws CoreException,
			UnsupportedEncodingException {
		file.setContents(new ByteArrayInputStream(contents.getBytes("UTF-8")),
				IResource.FORCE, monitor);
	}

	private static String getContents(IFile file) throws IOException, CoreException {
		return FileUtils.fileRead(file.getLocation().toFile(),
				file.getCharset());
	}
}
