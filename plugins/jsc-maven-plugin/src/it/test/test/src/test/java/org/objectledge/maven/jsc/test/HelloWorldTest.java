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
package org.objectledge.maven.jsc.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HelloWorldTest {
	@Test
	public void testHelloWorld() {
		IHelloWorld helloWorld = new HelloWorld();
		assertEquals("Hello world!", helloWorld.helloWorld());
	}
}