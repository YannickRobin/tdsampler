package com.ebizance.tdsampler.stack.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ebizance.tdsampler.context.TDSampler;
import com.ebizance.tdsampler.context.TDSamplerContext;
import com.ebizance.tdsampler.model.MethodNode;
import com.ebizance.tdsampler.service.TDSamplerResult;
import com.ebizance.tdsampler.service.TDSamplerService;
import com.ebizance.tdsampler.stack.impl.DefaultThreadDumpParserTest;

public class CallTreeTDSamplerServiceTest {

	private static final Logger LOG = Logger.getLogger(CallTreeTDSamplerServiceTest.class);
	private TDSamplerContext previousContext;
	@Before
	public void setup()
	{
		previousContext = TDSampler.getContext();
		final TDSamplerContext tdSamplerContext = new TDSamplerContext();
		//tdSamplerContext.setIncludeListThreadName("hybrisHTTP");
		tdSamplerContext.setIncludeListThreadState("RUNNABLE,IOWAIT,WAITING,TIMED_WAITING,BLOCKED");
		tdSamplerContext.setIncludeListMethod("");
		tdSamplerContext.setExcludeListMethod("org.apache.coyote,org.apache.catalina,org.apache.tomcat,java.util.concurrent.ThreadPoolExecutor");
		tdSamplerContext.setIncludeListThread("de.hybris.platform.servicelayer.web.XSSFilter.doFilter(*)");
		TDSampler.setContext(tdSamplerContext);
	}
	
	@After
	public void teardown()
	{
		// restore the previous context
		TDSampler.setContext(previousContext);
	}
	
	
	@Test
	public void callTreeTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(result.getMethods().size(), result.getMethodNodes().size());
	}
		
}
