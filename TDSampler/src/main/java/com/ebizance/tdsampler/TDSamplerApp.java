package com.ebizance.tdsampler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ebizance.tdsampler.context.TDSampler;
import com.ebizance.tdsampler.context.TDSamplerContext;
import com.ebizance.tdsampler.exception.TDSamplerException;
import com.ebizance.tdsampler.model.MethodNode;
import com.ebizance.tdsampler.model.Thread;
import com.ebizance.tdsampler.service.TDSamplerResult;
import com.ebizance.tdsampler.service.TDSamplerService;

/**
 * Command line application to run Thread dump sampler.<br/><br/>
 * 
 * <code>
 * Usage: java -Dlog4j.configuration=file:../conf/log4j.properties -jar ../lib/TDSampler-1.0-SNAPSHOT.jar [file|dir]<br/><br/>
 * 
 *
 * -Arguments-<br/>
 * file: Specify stack file path (e.g: C:/temp/test05-30-3/td.30052012_14_00_57.txt). The application will parse the indicated file only.<br/>
 * or<br/>
 * dir: Specify folder path (e.g: C:/temp/test05-30-3). The application will parse all thread dumps inside the indicated folder.<br/>
 * </code>
 * 
 * @author Yannick Robin
 *
 */
public class TDSamplerApp 
{
    private static final String CONFIGURATION_FILE_DEFAULT = ".." + File.separator + "conf" + File.separator + "conf.properties"; 
	private static final Logger logger = Logger.getLogger(TDSamplerApp.class);
	private static final int DEPTH_LIMIT = 50;
	
	public static void main(String[] args)
    {    	
    	if (args.length == 0)
    	{
    		displayUsageMessage();
    		System.exit(-1);
    	}

    	try {
    		TDSampler.setContext(getConfiguredContext());
		} catch (FileNotFoundException e) {
    		displayUsageMessage();
    		System.out.println("Configuration file not found...");
    		System.exit(-1);
		} catch (IOException e) {
    		displayUsageMessage();
    		System.out.println("Configuration file not found...");
    		System.exit(-1);
		}
    	
		TDSamplerService tdSamplerService = new TDSamplerService();
		TDSamplerResult result;
		try {
			result = tdSamplerService.parse(args[0]);
		} catch (TDSamplerException e) {
			if (e.getMessage().startsWith("Path"))
			{
				displayUsageMessage();
				System.out.println("Thread dumps path not found...");
				System.exit(-1);
			}
			else {
				e.printStackTrace();
				System.out.println("Unable to parse thread dumps...");
				System.exit(-1);
			}	
			return;
		}
    	
    	System.out.println("***Thread dump sampler result for " + result.getThreadCounter() + " thread(s) ***");

    	if (args.length > 1 && args[1] != null && args[1].equals("-thread_report"))
    		displayThreads(result.getThreadStates());
    	displayMethods(result.getMethods(), result.getThreadCounter());
    	displayCallTree(result);
    }
	
	private static TDSamplerContext getConfiguredContext() throws FileNotFoundException, IOException
	{
    	Properties properties = new Properties();
    		
		String configurationFile;
		if (System.getenv("tdsampler.configuration") != null)
			configurationFile = System.getenv("tdsampler.configuration");
		else
			configurationFile = CONFIGURATION_FILE_DEFAULT;
		   			
		properties.load(new FileInputStream(configurationFile));
			
		TDSamplerContext tdSamplerContext = new TDSamplerContext();
		tdSamplerContext.setCountDuplicatedMethods(Boolean.parseBoolean(properties.getProperty("countDuplicateMethods", "false")));
		tdSamplerContext.setIncludeListThreadName(properties.getProperty("includeListThreadName", ""));
		tdSamplerContext.setIncludeListThreadState(properties.getProperty("includeListThreadState", ""));
		tdSamplerContext.setIncludeListMethod(properties.getProperty("includeListMethod", ""));
		tdSamplerContext.setExcludeListMethod(properties.getProperty("excludeListMethod", ""));
		tdSamplerContext.setIncludeListThread(properties.getProperty("includeListThread", ""));
		tdSamplerContext.setExcludeListThread(properties.getProperty("excludeListThread", ""));
		tdSamplerContext.setIncludeListIOWait(properties.getProperty("includeListIOWait", ""));
		tdSamplerContext.setImplementationClass(properties.getProperty("implementationClass",""));
		tdSamplerContext.setExcludeListThread("java.lang.ref.Reference$ReferenceHandler.run(*),java.util.concurrent.ThreadPoolExecutor.getTask(*),de.hybris.platform.util.threadpool.PoolableThread.resetAndReturnToPool(*),org.apache.tomcat.util.threads.TaskQueue.take(*),java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(*)");
		tdSamplerContext.setExcludeListMethod("org.apache,java.lang.reflect,sun.reflect,java.util.concurrent.ThreadPoolExecutor,javax.servlet,org.springframework,de.hybris.platform.servicelayer.web,java.lang.Thread");
		tdSamplerContext.setIncludeListThread("de.hybris.platform.servicelayer.web.XSSFilter.doFilter(*)");
		return tdSamplerContext;
	}

	private static void displayUsageMessage()
    {
		System.out.println("\nUsage: java -Dlog4j.configuration=file:../conf/log4j.properties -jar ../lib/TDSampler-1.0-SNAPSHOT.jar [file|dir]");
		System.out.println("\n-Arguments-");
		System.out.println("file\t Specify stack file path (e.g: C:/temp/test05-30-3/td.30052012_14_00_57.txt). The application will parse the indicated file only.");
		System.out.println("or");
		System.out.println("dir\t Specify folder path (e.g: C:/temp/test05-30-3). The application will parse all thread dumps inside the indicated folder.");
		System.out.println("");
    }
        
    private static void displayThreads(Map<Integer, Integer[]> threadStates)
    {
    	System.out.println("Thread state report:");
    	System.out.println("ID\tR\tIO\tW\tTM\tB\tU");
    	
        Vector<Integer> v = new Vector<Integer>(threadStates.keySet());
        Collections.sort(v);

    	for (Iterator it= v.iterator(); it.hasNext(); )
    	{
    		Integer threadDumpId = (Integer)it.next();
    		Integer[] states = threadStates.get(threadDumpId);

    		System.out.println(threadDumpId + "\t"
    				+ states[Thread.STATE_RUNNABLE] + "\t"
    				+ states[Thread.STATE_IOWAIT] + "\t"    				
    				+ states[Thread.STATE_WAITING] + "\t"
    				+ states[Thread.STATE_TIMED_WAITING] + "\t"
    				+ states[Thread.STATE_BLOCKED] + "\t"
    				+ states[Thread.STATE_UNKNOWN]);
    	}
	}
    
    private static void displayMethods(final Map<String,Integer> methods, int threadCounter)
    {
    	System.out.println("Hotspot method report:");
		System.out.println("rank\tcount\tpercent\tmethod");
		int i=1;
    	for (Iterator <Map.Entry<String, Integer>> it=methods.entrySet().iterator(); it.hasNext(); )
    	{
			Map.Entry<String, Integer> entry = it.next();
    		String method = entry.getKey();
    		Integer counter = entry.getValue();
    		Integer percent = (int)((counter * 100.0f) / threadCounter);;
    		System.out.println(i + "\t" + counter + "\t" + percent + "%\t" + method);
    		i++;
    	} 
    }
    
    private static String getResourceAsString(String resource)
    {
    	InputStream stream = TDSamplerApp.class.getResourceAsStream(resource);
    	try {
			return IOUtils.toString(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load resource: " + resource, e);
		}
    }
    
    private static void displayCallTree(TDSamplerResult tdSamplerResult)
    {
    	String jsonTree = createJSONCallTree(tdSamplerResult);
    	
    	
    	
		FileWriter fileWriter;
		try {
			File callTreeFile = new File("calltree-" + System.currentTimeMillis() + ".html");
			System.out.println("Creating HTML Call Tree");
			fileWriter = new FileWriter(callTreeFile);
			fileWriter.write("<!DOCTYPE html>");
			fileWriter.write("<html>\n");
			fileWriter.write("<head>\n");
			fileWriter.write("<title>\n");
			fileWriter.write("TDSampler Call Tree\n");
			fileWriter.write("</title>\n");
			//fileWriter.write("<script src=\"jquery-1.7.2.min.js\"></script>\n");
			//fileWriter.write("<script src=\"tree.jquery.js\"></script>\n");
			fileWriter.write("<style>\n");
			fileWriter.write(getResourceAsString("/calltree/jqtree.css"));
			fileWriter.write("</style>\n");
			fileWriter.write("</head>\n");
			fileWriter.write("<body>\n");
			fileWriter.write("Thread Count: " + tdSamplerResult.getThreadCounter());
			fileWriter.write("<div id=\"callTree\"></div>\n");
			fileWriter.write("<script>\n");
			fileWriter.write(getResourceAsString("/calltree/jquery-1.7.2.min.js") + "\n");
			fileWriter.write(getResourceAsString("/calltree/tree.jquery.js") + "\n");
			fileWriter.write("var data = " + jsonTree);
			fileWriter.write("\n$(function() {\n");
			fileWriter.write("$('#callTree').tree({\n");
			fileWriter.write("data: data, autoEscape: false\n");
			fileWriter.write("});\n");
			fileWriter.write("});\n");
			fileWriter.write("</script>\n");
			fileWriter.write("</body>\n");
			fileWriter.write("</html>\n");
			fileWriter.close();
			System.out.println("File created: " + callTreeFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static String createJSONCallTree(final TDSamplerResult result)
	{
		final JSONArray nodeArray = new JSONArray();
		final int depth = 1;
		for (final MethodNode node : result.getRootMethods())
		{
			try
			{
				processNode(node, 0, result, nodeArray, depth);
			}
			catch (final IOException e)
			{
				throw new IllegalStateException("Couldn't create call tree in JSON", e);
			}
		}

		String jsonString = nodeArray.toJSONString();
		jsonString = jsonString.replaceAll("<\\\\/", "</");
		return jsonString;
	}

	protected static boolean containsNodeObj(final JSONArray nodeArray, final String method)
	{
		if (CollectionUtils.isNotEmpty(nodeArray))
		{
			for (final Object obj : nodeArray)
			{
				final JSONObject nodeObj = (JSONObject) obj;
				if (((String) nodeObj.get("label")).startsWith(method))
				{
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected static void processNode(final MethodNode methodNode, final int level, final TDSamplerResult result, final JSONArray nodeArray, final int depth) throws IOException
	{
		if (containsNodeObj(nodeArray, methodNode.getMethod()))
		{
			return;
		}

		final JSONObject nodeObj = new JSONObject();
		nodeObj.put(
				"label",
				methodNode.getMethod() + "     <span class='percentageColumn'>" + methodNode.getCount() * 100
						/ result.getThreadCounter() + "%</span> <span class='countColumn'>" + methodNode.getCount() + "</span>");

		nodeArray.add(nodeObj);

		//processedMethods.add(methodNode.getMethod());
		if (depth < DEPTH_LIMIT)
		{
			if (!methodNode.getSubMethodNodes().isEmpty())
			{
				final JSONArray childArray = new JSONArray();
				nodeObj.put("children", childArray);
				for (final MethodNode childNode : methodNode.getSubMethodNodes())
				{
					processNode(childNode, level + 1, result, childArray, depth + 1);
				}
			}
		}
    }
}
