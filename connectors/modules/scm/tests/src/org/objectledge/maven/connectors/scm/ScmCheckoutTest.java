package org.objectledge.maven.connectors.scm;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

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
