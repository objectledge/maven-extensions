package org.objectledge.maven.ckpackager;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
	 * Location of of ckpackager configuration file.
	 * 
	 * @parameter expression="${basedir}/src/main/resources/ckeditor.pack"
	 * @required
	 */
	private String packScript;

	public void execute() throws MojoExecutionException, MojoFailureException {
		com.ckeditor.ckpackager.ckpackager.main(new String[] { packScript });
	}
}
