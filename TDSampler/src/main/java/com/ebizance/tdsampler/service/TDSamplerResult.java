package com.ebizance.tdsampler.service;

import java.util.Map;
import java.util.Set;

import com.ebizance.tdsampler.model.MethodNode;


public class TDSamplerResult
{
	private int threadCounter;
	private Map<Integer, Integer[]> threadStates;
	private Map<String, Integer> methods;
	private Map<Integer, MethodNode> methodNodes;
	private Set<MethodNode> rootMethods;
	
	/**
	 * @param threadCounter
	 * @param threadStates
	 * @param methods
	 */
	public TDSamplerResult(final int threadCounter, final Map<Integer, Integer[]> threadStates, final Map<String, Integer> methods, final Map<Integer, MethodNode> methodNodes, final Set<MethodNode> rootMethods)
	{
		this.threadCounter = threadCounter;
		this.threadStates = threadStates;
		this.methods = methods;
		this.methodNodes = methodNodes;
		this.rootMethods = rootMethods;
	}

	/**
	 * @return the threadCounter
	 */
	public int getThreadCounter()
	{
		return threadCounter;
	}

	/**
	 * @param threadCounter
	 *           the threadCounter to set
	 */
	public void setThreadCounter(final int threadCounter)
	{
		this.threadCounter = threadCounter;
	}

	/**
	 * @return the threadStates
	 */
	public Map<Integer, Integer[]> getThreadStates()
	{
		return threadStates;
	}

	/**
	 * @param threadStates
	 *           the threadStates to set
	 */
	public void setThreadStates(final Map<Integer, Integer[]> threadStates)
	{
		this.threadStates = threadStates;
	}

	/**
	 * @return the methods
	 */
	public Map<String, Integer> getMethods()
	{
		return methods;
	}

	/**
	 * @param methods
	 *           the methods to set
	 */
	public void setMethods(final Map<String, Integer> methods)
	{
		this.methods = methods;
	}

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
}