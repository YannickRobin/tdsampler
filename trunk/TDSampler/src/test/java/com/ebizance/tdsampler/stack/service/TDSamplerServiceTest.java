package com.ebizance.tdsampler.stack.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.ebizance.tdsampler.service.TDSamplerResult;
import com.ebizance.tdsampler.service.TDSamplerService;
import com.ebizance.tdsampler.stack.impl.ThreadDumpImplTest;

public class TDSamplerServiceTest {

	@SuppressWarnings("unused")
	@Test
	public void parseTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(resourceDirPath + ThreadDumpImplTest.THREAD_DUMP_FILENAME_1);
		
		assertEquals(2, result.getThreadCounter());
		assertEquals(18, result.getMethods().size());
		
		Map<String, Integer> methods = result.getMethods();
		assertNotNull(methods.get("java.lang.Thread.run(Thread.java:662)"));
		assertEquals(2, methods.get("java.lang.Thread.run(Thread.java:662)").intValue());
	}
}
