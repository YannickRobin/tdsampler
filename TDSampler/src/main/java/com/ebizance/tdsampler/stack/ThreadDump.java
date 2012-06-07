package com.ebizance.tdsampler.stack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerConfig;
import com.ebizance.tdsampler.TDSamplerUtil;
import com.ebizance.tdsampler.model.Thread;

/**
 * 
 * This abstract class is responsible for the thread dump parsing.<br/>
 *  
 * @author Yannick Robin
 * 
 */

public abstract class ThreadDump {

	private Map<String, Integer> methods = new LinkedHashMap<String, Integer>(); 
    private static final Logger logger = Logger.getLogger(ThreadDump.class);
    private BufferedReader in = null;
    private int threadCounter = 0;
    private int threadCounterRunnable = 0;
    private int threadCounterWaiting = 0;
    private int threadCounterTimedWaiting = 0;
    private int threadCounterBlocked = 0;
	private int threadCounterIOWait = 0;
    private int threadCounterUnknown = 0;
    
	public ThreadDump(String filePath)
	{
		try {
		    in = new BufferedReader(new FileReader(filePath));
		    String str;
		    while ((str = in.readLine()) != null && !str.contains("Full thread dump")) {
		    	//doNothing
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Thread thread = new Thread();
		
		//Thread name
		String[] tokens = str.split("[\"]");
		String threadName = tokens[1];
		if (threadName == null || threadName.equals(""))
			return;
		thread.setName(threadName);
		
		//Thread state	
		str = in.readLine();
		tokens = str.split("[:( ]");
		if (tokens.length > 5)
		{
			String threadState = tokens[5];
			if (threadState == null || threadState.equals(""))
				return;
			thread.setState(threadState);
		}
		else
			return;
		
		if (!isValidThreadHeader(thread))
			return;
		
		while ((str = in.readLine()) != null && str.contains("at ") || str.contains("- ") ) {
			if (str.contains("at "))
			{				
				String method = str.substring("	at ".length(), str.length());
				if (isValidMethod(str))
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

		threadCounter++;
		int state  = thread.getState();		
		switch(state) {
			case Thread.STATE_RUNNABLE:
				threadCounterRunnable++;
				break;
			case Thread.STATE_WAITING:
				threadCounterWaiting++;
				break;
			case Thread.STATE_TIMED_WAITING:
				threadCounterTimedWaiting++;
				break;
			case Thread.STATE_BLOCKED:
				threadCounterBlocked++;
				break;
			case Thread.STATE_IOWAIT:
				threadCounterIOWait ++;
				break;				
			case Thread.STATE_UNKNOWN:
				threadCounterUnknown++;
		}
				
		TDSamplerUtil.mergeMethods(methods, thread.getMethods());
	}
	
	public Map<String, Integer> getMethods()
	{
		return methods;
	}
	
	public int getThreadCounter() {
		return threadCounter;
	}
	
    public int getThreadCounterRunnable() {
		return threadCounterRunnable;
	}

	public int getThreadCounterWaiting() {
		return threadCounterWaiting;
	}

	public int getThreadCounterTimedWaiting() {
		return threadCounterTimedWaiting;
	}

	public int getThreadCounterBlocked() {
		return threadCounterBlocked;
	}

	public int getThreadCounterIOWait() {
		return threadCounterIOWait;
	}

	public int getThreadCounterUnknown() {
		return threadCounterUnknown;
	}
	
	public abstract boolean isValidThreadHeader(Thread thread);
	public abstract boolean isValidThread(Thread thread);
	public abstract boolean isValidMethod(String str);
}
