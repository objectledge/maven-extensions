package org.objectledge.maven.connectors.jflex.tests;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class SampleScannerTest {

	@Test
	public void testScanner() throws IOException {
		SampleScanner scanner = new SampleScanner(
				new StringReader("AAABbccCDD"));
		Assert.assertEquals(SampleScanner.UPPER, scanner.yylex());
		Assert.assertEquals("AAAB", scanner.yytext());
		Assert.assertEquals(SampleScanner.LOWER, scanner.yylex());
		Assert.assertEquals("bcc", scanner.yytext());
		Assert.assertEquals(SampleScanner.UPPER, scanner.yylex());
		Assert.assertEquals("CDD", scanner.yytext());
		Assert.assertEquals(SampleScanner.YYEOF, scanner.yylex());
	}

	@Test
	public void testInvalidInput() throws IOException {
		SampleScanner scanner = new SampleScanner(new StringReader("X7"));
		Assert.assertEquals(SampleScanner.UPPER, scanner.yylex());
		try {
			scanner.yylex();
			Assert.fail("Error should be thrown");
		} catch (Error e) {
			// OR;
		}
	}
}
