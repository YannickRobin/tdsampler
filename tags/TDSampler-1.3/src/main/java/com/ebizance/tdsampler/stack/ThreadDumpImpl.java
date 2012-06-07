package com.ebizance.tdsampler.stack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerConfig;
import com.ebizance.tdsampler.TDSamplerUtil;
import com.ebizance.tdsampler.model.Thread;

public class ThreadDumpImpl extends ThreadDump {
	
    private static final Logger logger = Logger.getLogger(ThreadDumpImpl.class);
    
	public ThreadDumpImpl(String filePath)
	{
		super(filePath);
	}

	@Override
	public boolean isValidThreadHeader(Thread thread) {		
		return isValidThreadName(thread) && isValidThreadState(thread);
	}
		
	@Override
	public boolean isValidThread(Thread thread) {
		
		if (thread.getMethods().size() == 0)
			return false;
		
		setIOWaitState(thread);
		//check state thread state again (for IOWait)
		if (!isValidThreadState(thread))
			return false;
		
		//exclude thread
		String[] tokensExcludeListThread = TDSamplerConfig.includeListThread_.split("[,]");
		
		for (int i=0; i<tokensExcludeListThread.length;i++)
		{
			if (thread.getMethods().containsKey(tokensExcludeListThread[i]))
				return false;
		}
		
		//include thread
		if (TDSamplerConfig.includeListThread_.equals(""))
			return true;
		
		String[] tokensIncludeListThread = TDSamplerConfig.includeListThread_.split("[,]");
		
		for (int i=0; i<tokensIncludeListThread.length;i++)
		{
			if (thread.getMethods().containsKey(tokensIncludeListThread[i]))
				return true;
		}	
		
		return false;				
	}

	private boolean isValidThreadName(Thread thread) {
		if (TDSamplerConfig.includeListThreadName_.equals(""))
			return true;
		
		String[] tokensIncludeList = TDSamplerConfig.includeListThreadName_.split("[,]");
		
		for (int i=0; i<tokensIncludeList.length;i++)
		{
			if (thread.getName().contains(tokensIncludeList[i]))
				return true;
		}
		
		return false;
	}
	
	private boolean isValidThreadState(Thread thread) {
		if (TDSamplerConfig.includeListThreadState_.equals(""))
			return true;
		
		String[] tokensIncludeList = TDSamplerConfig.includeListThreadState_.split("[,]");
		
		for (int i=0; i<tokensIncludeList.length;i++)
		{
			int state = TDSamplerUtil.getState(tokensIncludeList[i]);
			if (thread.getState() == state)
				return true;
		}
		
		return false;
	}
	
	//I/O Wait thread has RUNNABLE state but is an interruptible sleep 
	private void setIOWaitState(Thread thread)
	{
		if (isIOWaitState(thread))
			thread.setState(Thread.STATE_IOWAIT);
	}
	
	//I/O Wait thread has RUNNABLE state but is an interruptible sleep 
	private boolean isIOWaitState(Thread thread)
	{
		String firstKey = "";
		Iterator<String> iterator = thread.getMethods().keySet().iterator();
		if (iterator.hasNext())
			firstKey = iterator.next();
		
		if (TDSamplerConfig.includeListIOWait_.equals(""))
			return false;
		
		String[] tokensIncludeListIOWait = TDSamplerConfig.includeListIOWait_.split("[,]");
		
		for (int i=0; i<tokensIncludeListIOWait.length;i++)
		{
			if (thread.getMethods().containsKey(tokensIncludeListIOWait[i]))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isValidMethod(String str)
	{
		//Include IOWait thread (mandatory for isIOWaitState validation)		
		String[] tokensIncludeListIOWait = TDSamplerConfig.includeListIOWait_.split("[,]");
		
		for (int i=0; i<tokensIncludeListIOWait.length;i++)
		{
			if (str.contains(tokensIncludeListIOWait[i]))
				return true;
		}
		
		
		//Exclude methods
		if (!TDSamplerConfig.excludeListMethod_.equals(""))
		{	
			String[] tokensExcludeListMethod = TDSamplerConfig.excludeListMethod_.split("[,]");
			
			for (int i=0; i<tokensExcludeListMethod.length;i++)
			{
				if (str.contains(tokensExcludeListMethod[i]))
					return false;
			}	
		}
		
		//Include methods
		if (TDSamplerConfig.includeListMethod_.equals(""))
			return true;
		
		String[] tokensIncludeListMethod = TDSamplerConfig.includeListMethod_.split("[,]");
		
		for (int i=0; i<tokensIncludeListMethod.length;i++)
		{
			if (str.contains(tokensIncludeListMethod[i]))
				return true;
		}	
		
		return false;
	}

}
