package org.objectledge.maven.jsc.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HelloWorldTest
{
	@Test
	public void testHelloWorld()
	{
		IHelloWorld helloWorld = new HelloWorld(); 
		assertEquals("Hello world!", helloWorld.helloWorld());
	}
}