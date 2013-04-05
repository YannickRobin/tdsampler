package com.ebizance.tdsampler.service;

public class TDSamplerRequest
{
	String path;
	boolean countDuplicateMethods;
	String includeListThreadName;
	String includeListThreadState;
	String includeListMethod;
	String excludeListMethod;
	String includeListThread;
	String excludeListThread;
	String includeListIOWait;
	String implementationClass;

	public TDSamplerRequest()
	{
		//NO PMD
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param path
	 *           the path to set
	 */
	public void setPath(final String path)
	{
		this.path = path;
	}

	/**
	 * @return the countDuplicateMethods
	 */
	public boolean isCountDuplicateMethods()
	{
		return countDuplicateMethods;
	}

	/**
	 * @param countDuplicateMethods
	 *           the countDuplicateMethods to set
	 */
	public void setCountDuplicateMethods(final boolean countDuplicateMethods)
	{
		this.countDuplicateMethods = countDuplicateMethods;
	}

	/**
	 * @return the includeListThreadName
	 */
	public String getIncludeListThreadName()
	{
		return includeListThreadName;
	}

	/**
	 * @param includeListThreadName
	 *           the includeListThreadName to set
	 */
	public void setIncludeListThreadName(final String includeListThreadName)
	{
		this.includeListThreadName = includeListThreadName;
	}

	/**
	 * @return the includeListThreadState
	 */
	public String getIncludeListThreadState()
	{
		return includeListThreadState;
	}

	/**
	 * @param includeListThreadState
	 *           the includeListThreadState to set
	 */
	public void setIncludeListThreadState(final String includeListThreadState)
	{
		this.includeListThreadState = includeListThreadState;
	}

	/**
	 * @return the includeListMethod
	 */
	public String getIncludeListMethod()
	{
		return includeListMethod;
	}

	/**
	 * @param includeListMethod
	 *           the includeListMethod to set
	 */
	public void setIncludeListMethod(final String includeListMethod)
	{
		this.includeListMethod = includeListMethod;
	}

	/**
	 * @return the excludeListMethod
	 */
	public String getExcludeListMethod()
	{
		return excludeListMethod;
	}

	/**
	 * @param excludeListMethod
	 *           the excludeListMethod to set
	 */
	public void setExcludeListMethod(final String excludeListMethod)
	{
		this.excludeListMethod = excludeListMethod;
	}

	/**
	 * @return the includeListThread
	 */
	public String getIncludeListThread()
	{
		return includeListThread;
	}

	/**
	 * @param includeListThread
	 *           the includeListThread to set
	 */
	public void setIncludeListThread(final String includeListThread)
	{
		this.includeListThread = includeListThread;
	}

	/**
	 * @return the excludeListThread
	 */
	public String getExcludeListThread()
	{
		return excludeListThread;
	}

	/**
	 * @param excludeListThread
	 *           the excludeListThread to set
	 */
	public void setExcludeListThread(final String excludeListThread)
	{
		this.excludeListThread = excludeListThread;
	}

	/**
	 * @return the includeListIOWait
	 */
	public String getIncludeListIOWait()
	{
		return includeListIOWait;
	}

	/**
	 * @param includeListIOWait
	 *           the includeListIOWait to set
	 */
	public void setIncludeListIOWait(final String includeListIOWait)
	{
		this.includeListIOWait = includeListIOWait;
	}

	/**
	 * @return the implementationClass
	 */
	public String getImplementationClass()
	{
		return implementationClass;
	}

	/**
	 * @param implementationClass
	 *           the implementationClass to set
	 */
	public void setImplementationClass(final String implementationClass)
	{
		this.implementationClass = implementationClass;
	}

}