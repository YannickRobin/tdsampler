package com.ebizance.tdsampler.stack.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class DefaultThreadDumpParserTest {

	
	public final static String THREAD_DUMP_FILENAME = "default_threaddump_test.txt";
	
	@Test
	public void parseThreadTest() throws IOException
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		DefaultThreadDumpParser threadDumpImpl = new DefaultThreadDumpParser();
		threadDumpImpl.parse(resourceDirPath + THREAD_DUMP_FILENAME);
		
		assertEquals(2, threadDumpImpl.getThreadCounter());
		assertEquals(2, threadDumpImpl.getThreadCounterRunnable());
		assertEquals(18, threadDumpImpl.getMethods().size());
		
		Map<String, Integer> methods = threadDumpImpl.getMethods();
		assertNotNull(methods.get("java.lang.Thread.run(Thread.java:662)"));
		assertEquals(2, methods.get("java.lang.Thread.run(Thread.java:662)").intValue());
	}
}
