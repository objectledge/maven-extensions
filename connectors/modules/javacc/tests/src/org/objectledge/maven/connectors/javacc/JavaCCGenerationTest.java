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
package org.objectledge.maven.connectors.javacc;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

@SuppressWarnings("restriction")
public class JavaCCGenerationTest extends AbstractMavenProjectTestCase {
	public void test_01() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
		IProject project1 = importProject("projects/javacc-01/pom.xml",
				configuration);
		waitForJobsToComplete();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		assertNoErrors(project1);

		IJavaProject javaProject1 = JavaCore.create(project1);
		IClasspathEntry[] cp1 = javaProject1.getRawClasspath();

		assertEquals(new Path("/javacc-01/target/generated-sources/javacc"),
				cp1[4].getPath());

		assertTrue(project1
				.getFile(
						"target/generated-sources/javacc/org/objectledge/maven/connectors/javacc/tests/SampleParser.java")
				.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue(project1
				.getFile(
						"target/generated-sources/javacc/org/objectledge/maven/connectors/javacc/tests/SampleParser.java")
				.isAccessible());
	}
}
