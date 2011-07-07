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
/*
 * A similified ckpackager launcher for embedded mode
 */

importClass(java.io.File);
importClass(java.io.BufferedWriter);
importClass(java.io.FileWriter);

loadClass("ckpackager.includes.ckpackager");

CKPACKAGER.isCompiled = true;

function error(msg) {
	print(msg);
	print('');
}

if (arguments.length > 1
		&& (arguments[arguments.length - 1] == '-v' || arguments[arguments.length - 1] == '--verbose'))

	CKPACKAGER.verbose = 1;

if (arguments[0] == '-dump') {
	
	CKPACKAGER.load('ckpackager.includes.parser');
	
	CKPACKAGER.parser.dumpThree(CKPACKAGER.tools.readFile(arguments[1]));

} else if (arguments[0] == '-compress') {

	CKPACKAGER.load('ckpackager.includes.scriptcompressor');

	var compressed = CKPACKAGER.scriptCompressor.compress(CKPACKAGER.tools
			.readFile(arguments[1]));

	if (arguments[2]) {
		var out = new BufferedWriter(new FileWriter(arguments[2]));
		out.write(compressed);
		out.close();
	} else {
		print(compressed);
	}
	
} else {

	CKPACKAGER.packFile = arguments[0];
	var packFile = new File(CKPACKAGER.packFile);
	
	CKPACKAGER.packDir = packFile.getParent();

	if (arguments[1] == '-output') {
		CKPACKAGER.outputDir = new File(arguments[2]);
	} else {
		CKPACKAGER.outputDir = CKPACKAGER.packDir;		
	}

	CKPACKAGER.load('ckpackager.includes.packager');

	try {
		var packager = new CKPACKAGER.packager();
		packager.loadDefinitionFile(CKPACKAGER.packFile);
		packager.run();
	} catch (e) {
		if (CKPACKAGER.verbose) {
			if (typeof (e.rhinoException) != 'undefined') {
				e.rhinoException.printStackTrace();
			} else if (typeof (e.javaException) != 'undefined') {
				e.javaException.printStackTrace();
			}
		}
		error(e);
	}
}
