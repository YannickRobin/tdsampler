package com.ebizance.tdsampler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.context.TDSamplerContext;
import com.ebizance.tdsampler.exception.TDSamplerException;
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
	
	public static void main(String[] args)
    {    	
    	if (args.length == 0)
    	{
    		displayUsageMessage();
    		System.exit(-1);
    	}

    	try {
			setContext();
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
    }
	
	private static void setContext() throws FileNotFoundException, IOException
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
    
}
