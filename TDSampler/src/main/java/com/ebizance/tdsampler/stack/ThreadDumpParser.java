package com.ebizance.tdsampler.stack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ebizance.tdsampler.TDSamplerUtil;
import com.ebizance.tdsampler.context.TDSampler;
import com.ebizance.tdsampler.exception.TDSamplerException;
import com.ebizance.tdsampler.model.MethodNode;
import com.ebizance.tdsampler.model.Thread;

/**
 * 
 * This abstract class is responsible for the thread dump parsing.<br/>
 *  
 * @author Yannick Robin
 * 
 */

public abstract class ThreadDumpParser {

    private static final Logger logger = Logger.getLogger(ThreadDumpParser.class);
    
	protected Map<String, Integer> methods = new LinkedHashMap<String, Integer>(); 
	private Map<Integer, MethodNode> methodNodes;
	
	// call tree
	private Set<MethodNode> rootMethods;
    protected BufferedReader in = null;
    protected int threadCounter = 0;
    protected int threadCounterRunnable = 0;
    protected int threadCounterWaiting = 0;
    protected int threadCounterTimedWaiting = 0;
    protected int threadCounterBlocked = 0;
    protected int threadCounterIOWait = 0;
    protected int threadCounterUnknown = 0;
    protected int rootMethodCounter = 0;
    
	public ThreadDumpParser()
	{
	}
	
    public void parse(String filePath, Map<Integer, MethodNode>  methodNodes,  Set<MethodNode> rootMethodNodes)
    {
		logger.debug("File: " + filePath);
		try {
		    in = new BufferedReader(new FileReader(filePath));
		    String str;
		    while ((str = in.readLine()) != null && !str.contains("Full thread dump")) {
		    	//doNothing
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	 	
		this.rootMethods = rootMethodNodes;
		this.methodNodes = methodNodes;
		
    	String str;
		try {
			while ((str = in.readLine()) != null) {	
				if (str.contains("tid="))
					parseThread(str);
			}
		} catch (IOException | TDSamplerException e) {
			e.printStackTrace();
		}
    }
    
    private void assignNodeToParentNode(final MethodNode node, final MethodNode previousNode) throws TDSamplerException
    {
		if (previousNode != null) {
			node.setParentNode(previousNode);
			if (!previousNode.getSubMethodNodes().contains(node)) {
				previousNode.getSubMethodNodes().add(node);
			}
		}
    }
    
    public int hashCodeForMethod(String method, int parentHashCode) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + parentHashCode;
		return result;
	}
    
	private void parseThread(String str) throws IOException, TDSamplerException
	{
		logger.debug("Thread: " + str);
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
		
		while ((str = in.readLine()) != null && (str.contains("at ") || str.contains("- ")) ) {
					
			if (str.contains("at "))
			{				
				String method = str.substring("	at ".length(), str.length());
				thread.getFullMethodStack().add(0, method);
				if (isValidMethod(str))
				{
					Integer counter = (Integer)thread.getMethods().get(method);
					if (counter == null)
					{
						thread.getMethods().put(method, 1);	
					}
					else
					{
						if (TDSampler.getContext().isCountDuplicatedMethods())
						{
							thread.getMethods().put(method, counter + 1);	
						}
					}
					
				}	
			}
		}
		
		if (!isValidThread(thread))
		{
			return;
		}
		else
		{
			// update the call tree
			MethodNode node = null;
			MethodNode previousMethodNode = null;
			if (CollectionUtils.isEmpty(thread.getFullMethodStack()))
			{
				logger.error("No stack found");
			}
			int parentHashCode = 0;
			for (String method : thread.getFullMethodStack())
			{
				if (isValidMethod(method)) {
					
					int methodPathHash = hashCodeForMethod(method, parentHashCode);
					parentHashCode = methodPathHash;
					if (methodNodes.containsKey(methodPathHash)) {
						node = methodNodes.get(methodPathHash);
						node.increaseCount();
					} else {
						node = new MethodNode(method, 1);
						methodNodes.put(methodPathHash, node);
					}
					
					assignNodeToParentNode(node, previousMethodNode);
					
					// root of the thread
					if (previousMethodNode == null) {
						rootMethodCounter++;
						rootMethods.add(node);
					}
					previousMethodNode = node;
				}
			}
		}
		
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

	
	public Set<MethodNode> getRootMethods() {
		return rootMethods;
	}

	public void setRootMethods(Set<MethodNode> rootMethods) {
		this.rootMethods = rootMethods;
	}

	public Map<Integer, MethodNode> getMethodNodes() {
		return methodNodes;
	}

	public void setMethodNodes(Map<Integer, MethodNode> methodNodes) {
		this.methodNodes = methodNodes;
	}

	/**
	 * @return the rootMethodCounter
	 */
	public int getRootMethodCounter() {
		return rootMethodCounter;
	}

	/**
	 * @param rootMethodCounter the rootMethodCounter to set
	 */
	public void setRootMethodCounter(int rootMethodCounter) {
		this.rootMethodCounter = rootMethodCounter;
	}
}
