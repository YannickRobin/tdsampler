package com.ebizance.tdsampler.stack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerUtil;
import com.ebizance.tdsampler.model.Thread;

public class ThreadDumpImpl extends ThreadDump {
	
    private static final Logger logger = Logger.getLogger(ThreadDumpImpl.class);
    private static final String CONFIGURATION_FILE_DEFAULT = "../conf/conf.properties"; 
    	
    static boolean countDuplicateMethods_;
    static String includeListThreadName_;    
    static String includeListThreadState_;    
    static String includeListMethod_;
    static String excludeListMethod_;
    static String includeListThread_;
    static String excludeListThread_;
    static String includeListIOWait_;
      
    static {
    	Properties properties = new Properties();
    	try {
    		
    		String configurationFile;
    		if (System.getenv("tdsampler.configuration") != null)
    			configurationFile = System.getenv("tdsampler.configuration");
    		else
    			configurationFile = CONFIGURATION_FILE_DEFAULT;
    		   			
			properties.load(new FileInputStream(configurationFile));
			countDuplicateMethods_ = Boolean.parseBoolean(properties.getProperty("countDuplicateMethods", "false"));
			includeListThreadName_ = properties.getProperty("includeListThreadName", "");
			includeListThreadState_ = properties.getProperty("includeListThreadState", "");
			includeListMethod_ = properties.getProperty("includeListMethod", "");
			excludeListMethod_ = properties.getProperty("excludeListMethod", "");
			includeListThread_ = properties.getProperty("includeListThread", "");
			excludeListThread_ = properties.getProperty("excludeListThread", "");
			includeListIOWait_ = properties.getProperty("includeListIOWait", "");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
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
		String[] tokensExcludeListThread = includeListThread_.split("[,]");
		
		for (int i=0; i<tokensExcludeListThread.length;i++)
		{
			if (thread.getMethods().containsKey(tokensExcludeListThread[i]))
				return false;
		}
		
		//include thread
		if (includeListThread_.equals(""))
			return true;
		
		String[] tokensIncludeListThread = includeListThread_.split("[,]");
		
		for (int i=0; i<tokensIncludeListThread.length;i++)
		{
			if (thread.getMethods().containsKey(tokensIncludeListThread[i]))
				return true;
		}	
		
		return false;				
	}

	private boolean isValidThreadName(Thread thread) {
		if (includeListThreadName_.equals(""))
			return true;
		
		String[] tokensIncludeList = includeListThreadName_.split("[,]");
		
		for (int i=0; i<tokensIncludeList.length;i++)
		{
			if (thread.getName().contains(tokensIncludeList[i]))
				return true;
		}
		
		return false;
	}
	
	private boolean isValidThreadState(Thread thread) {
		if (includeListThreadState_.equals(""))
			return true;
		
		String[] tokensIncludeList = includeListThreadState_.split("[,]");
		
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
		
		if (includeListIOWait_.equals(""))
			return false;
		
		String[] tokensIncludeListIOWait = includeListIOWait_.split("[,]");
		
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
		String[] tokensIncludeListIOWait = includeListIOWait_.split("[,]");
		
		for (int i=0; i<tokensIncludeListIOWait.length;i++)
		{
			if (str.contains(tokensIncludeListIOWait[i]))
				return true;
		}
		
		
		//Exclude methods
		if (!excludeListMethod_.equals(""))
		{	
			String[] tokensExcludeListMethod = excludeListMethod_.split("[,]");
			
			for (int i=0; i<tokensExcludeListMethod.length;i++)
			{
				if (str.contains(tokensExcludeListMethod[i]))
					return false;
			}	
		}
		
		//Include methods
		if (includeListMethod_.equals(""))
			return true;
		
		String[] tokensIncludeListMethod = includeListMethod_.split("[,]");
		
		for (int i=0; i<tokensIncludeListMethod.length;i++)
		{
			if (str.contains(tokensIncludeListMethod[i]))
				return true;
		}	
		
		return false;
	}

	@Override
	public boolean countDuplicateMethods() {
		return countDuplicateMethods_;
	}	
}
