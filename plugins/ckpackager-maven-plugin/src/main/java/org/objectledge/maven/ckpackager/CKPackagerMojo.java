package org.objectledge.maven.ckpackager;

import java.io.File;

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
	private File packScript;

	public void execute() throws MojoExecutionException, MojoFailureException {

		if (packScript.exists() && packScript.isFile() && packScript.canRead()) {
			getLog().info(
					"Processing packaging script "
							+ packScript.getAbsolutePath());

			String userDir = System.getProperty("user.dir");
			try {
				System.setProperty("user.dir", packScript.getParent());
				ckpackager.ckpackager.main(new String[] { packScript
						.getAbsolutePath() });
			} catch (RuntimeException e) {
				throw new MojoExecutionException(
						"ckpackager invocation failed", e);
			} finally {
				System.setProperty("user.dir", userDir);
			}
		} else {
			throw new MojoFailureException(packScript.getAbsolutePath()
					+ " does not exist or is unreadable");
		}

	}
}
