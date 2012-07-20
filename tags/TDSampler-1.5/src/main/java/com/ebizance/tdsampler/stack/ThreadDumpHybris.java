package com.ebizance.tdsampler.stack;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerConfig;
import com.ebizance.tdsampler.TDSamplerUtil;
import com.ebizance.tdsampler.model.Thread;

/**
 * 
 * @author Yannick Robin
 *
 */

public class ThreadDumpHybris extends ThreadDump {
	
    private static final Logger logger = Logger.getLogger(ThreadDumpImpl.class);
    
	public ThreadDumpHybris(String filePath)
	{
		super(filePath);
	}
	
    public void parse() throws IOException
    {
    	String str;
		while ((str = in.readLine()) != null) {	
			if (str.contains("tid="))
				parseThread(str);
		}
    }	
    
	private void parseThread(String str) throws IOException
	{
		logger.debug("Thread: " + str);
		Thread thread = new Thread();
		
		//Thread name
		String[] tokens = str.split("[\"]");
		
		String threadName = tokens[1];
		
		if (threadName == null || threadName.equals(""))
			return;
		thread.setName(threadName);
		
		logger.debug("Name: " + thread.getName());
		
		//Thread state	
		tokens = str.split("[\" ]");
		
		if (tokens.length > 6)
		{
			String threadState = tokens[6];
			if (threadState == null || threadState.equals(""))
				return;
			thread.setState(threadState);
		}
		else
			return;
		
		logger.debug("State: " + thread.getState());
		
		if (!isValidThreadHeader(thread))
			return;
		
		String strMethod = null;
		while ((strMethod = super.in.readLine()) != null 
				&& !strMethod.equals("")
				&& strMethod.contains("at ")
			) {
			logger.debug("method: " + strMethod);
			if (strMethod.contains("at "))
			{				
				String method = strMethod.substring("	at ".length(), strMethod.length());
				if (isValidMethod(strMethod))
				{
					Integer counter = (Integer)thread.getMethods().get(method);
					if (counter == null)
						thread.getMethods().put(method, 1);
					else
					{
						if (TDSamplerConfig.countDuplicateMethods_)
							thread.getMethods().put(method, counter + 1);
					}
				}
			}
		}
		
		if (!isValidThread(thread))
			return;
		
		super.threadCounter++;
		int state  = thread.getState();		
		switch(state) {
			case Thread.STATE_RUNNABLE:
				super.threadCounterRunnable++;
				break;
			case Thread.STATE_WAITING:
				super.threadCounterWaiting++;
				break;
			case Thread.STATE_TIMED_WAITING:
				super.threadCounterTimedWaiting++;
				break;
			case Thread.STATE_BLOCKED:
				super.threadCounterBlocked++;
				break;
			case Thread.STATE_IOWAIT:
				super.threadCounterIOWait ++;
				break;				
			case Thread.STATE_UNKNOWN:
				super.threadCounterUnknown++;
		}
				
		TDSamplerUtil.mergeMethods(super.methods, thread.getMethods());
	}
		
	@Override
	public boolean isValidThreadHeader(Thread thread) {		
		return isValidThreadName(thread) && isValidThreadState(thread);
	}
		
	@Override
	public boolean isValidThread(Thread thread) {
		
		if (thread.getMethods().size() == 0)
		{
			logger.debug("Thread no valid because no method.");
			return false;
		}
		
		setIOWaitState(thread);
		//check state thread state again (for IOWait)
		if (!isValidThreadState(thread))
		{
			logger.debug("Thread no valid because no valid state.");
			return false;
		}
		
		//exclude thread
		String[] tokensExcludeListThread = TDSamplerConfig.excludeListThread_.split("[,]");
		
		for (int i=0; i<tokensExcludeListThread.length;i++)
		{
			if (thread.getMethods().containsKey(tokensExcludeListThread[i]))
			{
				logger.debug("Thread no valid because into the exlude list.");
				return false;
			}
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
		logger.debug("Thread no valid because not into the include list.");
		
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
		
		logger.debug("Thread no valid thread name.");
		
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
		
		logger.debug("Thread no valid because no valid state.");
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
			{
				logger.debug("Method valid because IO-Wait.");
				return true;
			}
		}
		
		
		//Exclude methods
		if (!TDSamplerConfig.excludeListMethod_.equals(""))
		{	
			String[] tokensExcludeListMethod = TDSamplerConfig.excludeListMethod_.split("[,]");
			
			for (int i=0; i<tokensExcludeListMethod.length;i++)
			{
				if (str.contains(tokensExcludeListMethod[i]))
				{
					logger.debug("Method not valid because excluded.");
					return false;
				}
			}	
		}
		
		//Include methods
		if (TDSamplerConfig.includeListMethod_.equals(""))
		{
			logger.debug("Method valid because not includeListMethod parameter.");
			return true;
		}
		
		String[] tokensIncludeListMethod = TDSamplerConfig.includeListMethod_.split("[,]");
		
		for (int i=0; i<tokensIncludeListMethod.length;i++)
		{
			if (str.contains(tokensIncludeListMethod[i]))
			{
				logger.debug("Method valid because included.");
				return true;
			}
		}	
		
		return false;
	}
}
