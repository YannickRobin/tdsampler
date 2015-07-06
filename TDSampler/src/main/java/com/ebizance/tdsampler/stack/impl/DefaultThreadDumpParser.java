package com.ebizance.tdsampler.stack.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerUtil;
import com.ebizance.tdsampler.context.TDSampler;
import com.ebizance.tdsampler.model.Thread;
import com.ebizance.tdsampler.stack.ThreadDumpParser;

/**
 * 
 * @author Yannick Robin
 *
 */

public class DefaultThreadDumpParser extends ThreadDumpParser {
	
    private static final Logger logger = Logger.getLogger(DefaultThreadDumpParser.class);
    
    private Map<String, Pattern> patternsCache = new HashMap<String, Pattern>();
    
	public DefaultThreadDumpParser()
	{
		super();
	}

	@Override
	public boolean isValidThreadHeader(Thread thread) {		
		return isValidThreadName(thread) && isValidThreadState(thread);
	}
		
	@Override
	public boolean isValidThread(Thread thread) {
		
		if (thread.getMethods().size() == 0)
		{
			logger.debug("Thread no valid because no method");
			return false;
		}
		
		setIOWaitState(thread);
		//check state thread state again (for IOWait)
		if (!isValidThreadState(thread))
		{
			logger.debug("Thread no valid because no valid state");
			return false;
		}
		
		//exclude thread
		String[] tokensExcludeListThread = TDSampler.getContext().getExcludeListThread().split("[,]");
		if (!TDSampler.getContext().getExcludeListThread().equals("")) {
			for (int i = 0; i < tokensExcludeListThread.length; i++) {
				if (matches(thread.getFullMethodStack(), tokensExcludeListThread[i])) {
					logger.debug("Thread no valid because into the exlude list");
					return false;
				}
			}
		}
		
		//include thread
		if (TDSampler.getContext().getIncludeListThread().equals(""))
			return true;
		
		String[] tokensIncludeListThread = TDSampler.getContext().getIncludeListThread().split("[,]");
		
		for (int i=0; i<tokensIncludeListThread.length;i++)
		{
			if (matches(thread.getFullMethodStack(), tokensIncludeListThread[i]))
				return true;
		}
		logger.debug("Thread no valid because not into the include list");
		
		return false;				
	}

	private boolean isValidThreadName(Thread thread) {
		if (TDSampler.getContext().getIncludeListThreadName().equals(""))
			return true;
		
		String[] tokensIncludeList = TDSampler.getContext().getIncludeListThreadName().split("[,]");
		
		for (int i=0; i<tokensIncludeList.length;i++)
		{
			if (matches(thread.getName(),tokensIncludeList[i]))
				return true;
		}
		
		logger.debug("Thread no valid thread name");
		
		return false;
	}
	
	private boolean isValidThreadState(Thread thread) {
		if (TDSampler.getContext().getIncludeListThreadState().equals(""))
			return true;
		
		String[] tokensIncludeList = TDSampler.getContext().getIncludeListThreadState().split("[,]");
		
		for (int i=0; i<tokensIncludeList.length;i++)
		{
			int state = TDSamplerUtil.getState(tokensIncludeList[i]);
			if (thread.getState() == state)
				return true;
		}
		
		logger.debug("Thread no valid because no valid state");
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
		
		if (TDSampler.getContext().getIncludeListIOWait().equals(""))
			return false;
		
		String[] tokensIncludeListIOWait = TDSampler.getContext().getIncludeListIOWait().split("[,]");
		
		for (int i=0; i<tokensIncludeListIOWait.length;i++)
		{
			if (thread.getMethods().containsKey(tokensIncludeListIOWait[i]))
				return true;
		}
		
		return false;
	}
	
	protected boolean matches(List<String> methods, String pattern)
	{
		for (String method : methods)
		{
			if (matches(method, pattern))
			{
				return true;
			}
		}
		return false;
	}
	
	protected boolean matches(String method, String pattern)
	{
		String methodStripped = method.replaceFirst("\tat ", "");
		// regex wild card
		if (pattern.contains("*") )
		{
			
			if (patternsCache.containsKey(pattern))
			{
				return patternsCache.get(pattern).matcher(methodStripped).matches();
			}
			else
			{
				String fixedPattern = pattern.replaceAll("\\*", ".*");
				Pattern regExPattern = Pattern.compile(fixedPattern);
				patternsCache.put(pattern, regExPattern);
				return regExPattern.matcher(methodStripped).matches();
			}
		}
		else
		{
			return methodStripped.contains(pattern);
		}
	}
	
	@Override
	public boolean isValidMethod(String str)
	{		
		
		// Include IOWait thread (mandatory for isIOWaitState validation)
		if (!TDSampler.getContext().getIncludeListIOWait().equals("")) {
			String[] tokensIncludeListIOWait = TDSampler.getContext().getIncludeListIOWait().split("[,]");

			for (int i = 0; i < tokensIncludeListIOWait.length; i++) {
				if (matches(str,tokensIncludeListIOWait[i]))
					return true;
			}
		}
		// Exclude methods
		if (!TDSampler.getContext().getExcludeListMethod().equals("")) {
			String[] tokensExcludeListMethod = TDSampler.getContext().getExcludeListMethod().split("[,]");

			for (int i = 0; i < tokensExcludeListMethod.length; i++) {
				if (matches(str,tokensExcludeListMethod[i]))
					return false;
			}
		}

		// Include methods
		if (TDSampler.getContext().getIncludeListMethod().equals(""))
			return true;

		String[] tokensIncludeListMethod = TDSampler.getContext().getIncludeListMethod().split("[,]");

		for (int i = 0; i < tokensIncludeListMethod.length; i++) {
			if (matches(str,tokensIncludeListMethod[i]))
				return true;
		}

		return true;
	}

}
