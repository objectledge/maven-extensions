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
package org.objectledge.ckpackager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class PackScriptParser {

	private static final int BUFFER_SIZE = 16384;

	private static final String PACK_FILE_ENCODING = "UTF-8";

	private final Scriptable globalScope;

	public PackScriptParser() {
		Context cx = Context.enter();
		try {
			// initialize Rhino global context and save it for future use
			globalScope = cx.initStandardObjects();
		} finally {
			Context.exit();
		}
	}

	private static String readScript(File scriptFile)
			throws UnsupportedEncodingException, FileNotFoundException,
			IOException {
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(scriptFile),
					PACK_FILE_ENCODING);
			StringWriter writer = new StringWriter();
			int i = 0;
			char[] buff = new char[BUFFER_SIZE];
			while (i >= 0) {
				i = reader.read(buff);
				if (i > 0) {
					writer.write(buff, 0, i);
				}
			}
			return writer.toString();
		} catch (IOException e) {
			IOException ee = new IOException("failed to read script + "
					+ scriptFile.getAbsolutePath());
			ee.initCause(e);
			throw ee;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public void parseScript(File scriptFile, File outputDirectory,
			List<File> sourceFiles, List<File> outputFiles) throws IOException {
		File scriptDir = scriptFile.getParentFile();
		Context cx = Context.enter();
		try {
			// create a throwaway scope for parsing the script
			Scriptable scope = cx.newObject(globalScope);

			String scriptBody = readScript(scriptFile);
			cx.evaluateString(scope, "var script = {" + scriptBody + "\n};",
					scriptFile.getAbsolutePath(), 1, null);

			Scriptable script = (Scriptable) scope.get("script", scope);
			Scriptable packages = (Scriptable) script.get("packages", script);
			int i = 0;
			while (packages.has(i, packages)) {
				Scriptable packageDef = (Scriptable) packages
						.get(i++, packages);
				String output = (String) packageDef.get("output", packageDef);
				outputFiles.add(new File(outputDirectory, output));
				Scriptable files = (Scriptable) packageDef.get("files",
						packageDef);
				int j = 0;
				while (files.has(j, files)) {
					String file = (String) files.get(j++, files);
					sourceFiles.add(new File(scriptDir, file));
				}
			}
		} catch (Exception e) {
			IOException ee = new IOException("failed to parse script + "
					+ scriptFile.getAbsolutePath());
			ee.initCause(e);
			throw ee;
		} finally {
			Context.exit();
		}
	}
}
