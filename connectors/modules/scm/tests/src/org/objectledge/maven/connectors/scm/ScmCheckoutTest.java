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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

@SuppressWarnings("restriction")
public class ScmCheckoutTest extends AbstractMavenProjectTestCase {
	public void test_01() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
		IProject project1 = importProject("projects/testproject-01/pom.xml",
				configuration);
		waitForJobsToComplete();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		assertNoErrors(project1);

		IFile source = project1
				.getFile("target/checkout/src/org/objectledge/maven/connectors/scm/tests/HelloWorld.java");
		assertTrue(source.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue(source.isAccessible());

		IFile compiled = project1
				.getFile("target/classes/org/objectledge/maven/connectors/scm/tests/HelloWorld.class");
		assertTrue(compiled.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue(compiled.isAccessible());

	}
}
