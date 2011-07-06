package org.objectledge.maven.connectors.jflex;

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

public class JFlexProjectTest extends AbstractMavenProjectTestCase {
	public void test_01() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
		IProject project1 = importProject("projects/testproject-01/pom.xml",
				configuration);
		waitForJobsToComplete();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		assertNoErrors(project1);

		IJavaProject javaProject1 = JavaCore.create(project1);
		IClasspathEntry[] cp1 = javaProject1.getRawClasspath();

		assertEquals(
				new Path("/testproject-01/target/generated-sources/jflex"),
				cp1[3].getPath());

		IFile file = project1
				.getFile("target/generated-sources/jflex/org/objectledge/maven/connectors/jflex/tests/SampleScanner.java");
		assertTrue(file.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue(file.isAccessible());
	}
}
