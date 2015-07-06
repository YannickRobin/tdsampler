package com.ebizance.tdsampler.stack.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.ebizance.tdsampler.context.TDSampler;
import com.ebizance.tdsampler.context.TDSamplerContext;
import com.ebizance.tdsampler.service.TDSamplerResult;
import com.ebizance.tdsampler.service.TDSamplerService;
import com.ebizance.tdsampler.stack.impl.DefaultThreadDumpParserTest;

public class TDSamplerServiceTest {

	@Test
	public void parseDefaultTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		TDSampler.setContext(tdSamplerContext);
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(2, result.getThreadCounter());
		assertEquals(18, result.getMethods().size());
		
		Map<String, Integer> methods = result.getMethods();
		assertNotNull(methods.get("java.lang.Thread.run(Thread.java:662)"));
		assertEquals(2, methods.get("java.lang.Thread.run(Thread.java:662)").intValue());
	}
	
	@Test
	public void parseIncludeListThreadTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		//tdSamplerContext.setCountDuplicatedMethods(true);
		//tdSamplerContext.setIncludeListThreadName("");
		//tdSamplerContext.setIncludeListThreadState("");
		//tdSamplerContext.setIncludeListMethod("");
		//tdSamplerContext.setExcludeListMethod("");
		//tdSamplerContext.setIncludeListThread("");
		//tdSamplerContext.setExcludeListThread("");
		//tdSamplerContext.setIncludeListIOWait("");
		//tdSamplerContext.setImplementationClass("");
		tdSamplerContext.setIncludeListThread("sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:341)");
		TDSampler.setContext(tdSamplerContext);
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(1, result.getThreadCounter());
		assertEquals(8, result.getMethods().size());
	}
	
	@Test
	public void parseExcludeListThreadTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		tdSamplerContext.setExcludeListThread("sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:341)");
		TDSampler.setContext(tdSamplerContext);
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(1, result.getThreadCounter());
		assertEquals(11, result.getMethods().size());
	}
	
	@Test
	public void parseIncludeListThreadNameTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		tdSamplerContext.setIncludeListThreadName("TP-Processor32");
		TDSampler.setContext(tdSamplerContext);
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(1, result.getThreadCounter());
		assertEquals(11, result.getMethods().size());
	}
	
	@Test
	public void parseIncludeListThreadStateTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		tdSamplerContext.setIncludeListThreadState("RUNNABLE");
		TDSampler.setContext(tdSamplerContext);
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(2, result.getThreadCounter());
		assertEquals(18, result.getMethods().size());
	}
	
	@Test
	public void parseIncludeListThreadStateWithIOWaitTest() throws Exception
	{
		String resourceDirPath = "src" + File.separator + "main" + File.separator + "resource" + File.separator;
		String path = resourceDirPath + DefaultThreadDumpParserTest.THREAD_DUMP_FILENAME;
		
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		tdSamplerContext.setIncludeListThreadState("RUNNABLE");
		tdSamplerContext.setIncludeListIOWait("java.net.SocketInputStream.socketRead0(Native Method),java.net.SocketOutputStream.socketWrite0(Native Method),java.net.PlainSocketImpl.socketAccept(Native Method),java.net.PlainDatagramSocketImpl.receive0(Native Method)");
		TDSampler.setContext(tdSamplerContext);
		
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result = tdSamplerService.parse(path);
		
		assertEquals(0, result.getThreadCounter());
		assertEquals(0, result.getMethods().size());
	}	
}
