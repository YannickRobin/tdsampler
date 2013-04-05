package com.ebizance.tdsampler.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerUtil;

import com.ebizance.tdsampler.context.TDSampler;
import com.ebizance.tdsampler.exception.TDSamplerException;
import com.ebizance.tdsampler.model.Thread;
import com.ebizance.tdsampler.stack.ThreadDumpParser;

public class TDSamplerService {

	private static final Logger logger = Logger.getLogger(TDSamplerService.class);

	public TDSamplerResult parse(String path) throws TDSamplerException
	{
		
		final File file = new File(path);
		if (!file.exists())
		{
			throw new TDSamplerException("Path not found");
		}
		String[] stackFiles = null;
		String dirpath = null;

		if (file.isFile())
		{
			dirpath = file.getParent();
			stackFiles = new String[1];
			stackFiles[0] = file.getName();
		}
		else if (file.isDirectory())
		{
			dirpath = path;
			stackFiles = getStackFiles(path);
		}
		else
		{
			throw new TDSamplerException("Path is not a directory");
		}


		logger.info("");
		logger.info("***TD Prof parsing...");
		logger.info("");

		Map<String, Integer> methods = null;
		final Map<Integer, Integer[]> threadStates = new HashMap<Integer, Integer[]>();

		int threadCounter = 0;
		for (int i = 0; i < stackFiles.length; i++)
		{
			
			ThreadDumpParser threadDumpParser = TDSampler.getContext().getThreadDumpParser();
			logger.info("Parsing " + stackFiles[i] + "...");
			System.out.println("Parsing " + stackFiles[i] + "...");

			threadDumpParser.parse(path + File.separator + stackFiles[i]);

			threadCounter += threadDumpParser.getThreadCounter();
			final Integer[] threadStateCounter = new Integer[6];
			threadStateCounter[Thread.STATE_RUNNABLE] = Integer.valueOf(threadDumpParser.getThreadCounterRunnable());
			threadStateCounter[Thread.STATE_WAITING] = Integer.valueOf(threadDumpParser.getThreadCounterWaiting());
			threadStateCounter[Thread.STATE_TIMED_WAITING] = Integer.valueOf(threadDumpParser.getThreadCounterTimedWaiting());
			threadStateCounter[Thread.STATE_BLOCKED] = Integer.valueOf(threadDumpParser.getThreadCounterBlocked());
			threadStateCounter[Thread.STATE_IOWAIT] = Integer.valueOf(threadDumpParser.getThreadCounterIOWait());
			threadStateCounter[Thread.STATE_UNKNOWN] = Integer.valueOf(threadDumpParser.getThreadCounterUnknown());
			threadStates.put(Integer.valueOf(i), threadStateCounter);

			final Map<String, Integer> methodsTemp = threadDumpParser.getMethods();
			if (methods == null)
			{
				methods = methodsTemp;
			}
			else
			{
				TDSamplerUtil.mergeMethods(methods, methodsTemp);
			}
		}
		methods = TDSamplerUtil.sortHashMapByValues(methods, false);

		return new TDSamplerResult(threadCounter, threadStates, methods);
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

	
}
