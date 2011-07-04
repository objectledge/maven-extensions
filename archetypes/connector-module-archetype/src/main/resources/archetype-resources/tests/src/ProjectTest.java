#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

public class ProjectTest extends AbstractMavenProjectTestCase {
	public void test_01() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
        IProject project1 = importProject( "projects/testproject-01/pom.xml", configuration );
        waitForJobsToComplete();
        
        project1.build( IncrementalProjectBuilder.FULL_BUILD, monitor );
        project1.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );
        waitForJobsToComplete();

        assertNoErrors( project1 );
	}
}
