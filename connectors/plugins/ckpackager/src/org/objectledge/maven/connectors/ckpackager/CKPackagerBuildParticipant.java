package org.objectledge.maven.connectors.ckpackager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;

public class CKPackagerBuildParticipant extends MojoExecutionBuildParticipant {

	private static final int BUFFER_SIZE = 16384;

	private static final String PACK_FILE_ENCODING = "UTF-8";

	private static final Logger log = LoggerFactory
			.getLogger(CKPackagerBuildParticipant.class);

	public CKPackagerBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {

		IMaven maven = MavenPlugin.getMaven();
		BuildContext buildContext = getBuildContext();

		File packScript = maven.getMojoParameterValue(getSession(),
				getMojoExecution(), "packScript", File.class);
		File packScriptDir = packScript.getParentFile();

		if (packScript.exists() && packScript.isFile() && packScript.canRead()) {

			log.info("loading pack script");
			String script = "{" + readScript(packScript) + "}";
			JSONObject json = JSONObject.fromObject(script);
			JSONArray packages = json.getJSONArray("packages");
			log.info("detected " + packages.size() + " packages ");

			// if pack script changed we'll rebuild
			if (!buildContext.hasDelta(packScript)) {
				// pack script hasn't changed, check source files
				boolean sourcesChanged = false;
				for (File sourceFile : findSourceFiles(
						packScriptDir, packages)) {
					if (buildContext.hasDelta(sourceFile)) {
						sourcesChanged = true;
					}
				}
				if (!sourcesChanged) {
					log.info("nothing changed");
					return null;
				}
			}

			// execute mojo
			Set<IProject> result = super.build(kind, monitor);

			// refresh output files
			for(File outputFile : findOutputFiles(packScriptDir, packages)) {
				buildContext.refresh(outputFile);
			}
			
			return result;
		} else {
			throw new MojoFailureException("packScript " + packScript
					+ " does not exist or is unreadable");
		}

	}

	private String readScript(File packScript)
			throws UnsupportedEncodingException, FileNotFoundException,
			IOException {
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(packScript),
					PACK_FILE_ENCODING);
			StringWriter writer = new StringWriter();
			int i = 0;
			char[] buff = new char[BUFFER_SIZE];
			while (i > 0) {
				i = reader.read(buff);
				if (i > 0) {
					writer.write(buff, 0, i);
				}
			}
			return writer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private List<File> findOutputFiles(File baseDir, JSONArray packages) {
		List<File> paths = new ArrayList<File>();
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> i = packages.iterator();
		while (i.hasNext()) {
			JSONObject pkg = i.next();
			paths.add(new File(baseDir, pkg.getString("output")));
		}
		return paths;
	}

	private List<File> findSourceFiles(File baseDir, JSONArray packages) {
		List<File> paths = new ArrayList<File>();
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> i = packages.iterator();
		while (i.hasNext()) {
			JSONObject pkg = i.next();
			JSONArray files = pkg.getJSONArray("files");
			for (int j = 0; j < files.size(); j++) {
				paths.add(new File(baseDir, files.getString(j)));
			}
		}
		return paths;
	}
}
