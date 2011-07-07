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
package org.objectledge.maven.connectors.javacc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class SampleParserTest {

	@Test
	public void parseTest() throws ParseException {
		List<Integer> sequence = SampleParser.parse("1, 2, 3");
		assertEquals(3, sequence.size());
		assertEquals(1, sequence.get(0).intValue());
	}

	@Test
	public void parseExceptionTest() {
		try {
			SampleParser.parse("1, 2,,");
			fail("should throw ParseException");
		} catch (ParseException e) {
			// OK
		}
	}
}
