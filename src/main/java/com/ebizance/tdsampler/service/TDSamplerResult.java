package com.ebizance.tdsampler.service;

import java.util.Map;


public class TDSamplerResult
{
	private int threadCounter;
	private Map<Integer, Integer[]> threadStates;
	private Map<String, Integer> methods;

	/**
	 * @param threadCounter
	 * @param threadStates
	 * @param methods
	 */
	public TDSamplerResult(final int threadCounter, final Map<Integer, Integer[]> threadStates, final Map<String, Integer> methods)
	{
		this.threadCounter = threadCounter;
		this.threadStates = threadStates;
		this.methods = methods;
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
}