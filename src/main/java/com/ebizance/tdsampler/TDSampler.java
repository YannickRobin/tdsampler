package com.ebizance.tdsampler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.model.Thread;
import com.ebizance.tdsampler.stack.ThreadDump;
import com.ebizance.tdsampler.stack.ThreadDumpImpl;

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
public class TDSampler 
{
	private static final Logger logger = Logger.getLogger(TDSampler.class);
	
	public static void main(String[] args)
    {    	
    	if (args.length == 0)
    	{
    		displayUsageMessage();
    		System.exit(-1);
    	}
    	
    	String path = args[0];
    	File file = new File(path);
    	if (!file.exists())
    	{
    		displayUsageMessage();
	    	System.exit(-1);
    	}
    	String[] stackFiles = null;
    	String dirpath = null;
    	
    	if (file.isFile())
    	{
    		dirpath = file.getParent();
    		stackFiles = new String[1];
    		stackFiles[0]=file.getName();
    	}
    	else if (file.isDirectory())
    	{
    		dirpath = path;
    		stackFiles = getStackFiles(path);
    	}
    	else
    	{
    		displayUsageMessage();
	    	System.exit(-1);
    	}
    		
    	
    	logger.info("");
    	logger.info("***TD Prof parsing...");
    	logger.info("");
    	
    	Map<String,Integer> methods = null;
    	Map<Integer, Integer[]> threadStates = new HashMap<Integer, Integer[]>();
    	
    	int threadCounter = 0;
    	for (int i=0; i< stackFiles.length; i++)
    	{
    		ThreadDump threadDump = new ThreadDumpImpl(dirpath + File.separator + stackFiles[i]);
    		logger.info("Parsing " + stackFiles[i] + "...");
    		try {
    			threadDump.parse();
    		}
    		catch(Exception e)
    		{
    			logger.error(e);
    			e.printStackTrace();
    			System.exit(-1);
    		}
    		
    		threadCounter += threadDump.getThreadCounter();
    		Integer[] threadStateCounter = new Integer[6];
    		threadStateCounter[Thread.STATE_RUNNABLE]=threadDump.getThreadCounterRunnable();
    		threadStateCounter[Thread.STATE_WAITING]=threadDump.getThreadCounterWaiting();
    		threadStateCounter[Thread.STATE_TIMED_WAITING]=threadDump.getThreadCounterTimedWaiting();
    		threadStateCounter[Thread.STATE_BLOCKED]=threadDump.getThreadCounterBlocked();
    		threadStateCounter[Thread.STATE_IOWAIT]=threadDump.getThreadCounterIOWait();
    		threadStateCounter[Thread.STATE_UNKNOWN]=threadDump.getThreadCounterUnknown();
    		threadStates.put(i, threadStateCounter);
    		
    		Map<String,Integer> methodsTemp = threadDump.getMethods();
    		if (methods == null)
    			methods = methodsTemp;
    		else
    			TDSamplerUtil.mergeMethods(methods, methodsTemp);
    	}
    	methods = TDSamplerUtil.sortHashMapByValues(methods, false);
    	
    	System.out.println("***Thread dump sampler result for " + threadCounter + " thread(s) ***");
    	if (TDSamplerConfig.displayThreadStateReport_)
    		displayThreads(threadStates);
    	displayMethods(methods, threadCounter);
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
    
    private static String[] getStackFiles(String dirPath)
    {
    	File dir = new File(dirPath);

    	String[] children = dir.list();
    	if (children == null) {
    	    // Either dir does not exist or is not a directory
    	} else {
    	    for (int i=0; i<children.length; i++) {
    	        // Get filename of file or directory
    	        String filename = children[i];
    	    }
    	}
    	
    	return dir.list();
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
