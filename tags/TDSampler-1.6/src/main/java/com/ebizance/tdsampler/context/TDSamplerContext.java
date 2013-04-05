package com.ebizance.tdsampler.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.ebizance.tdsampler.stack.ThreadDumpParser;

public class TDSamplerContext
{
    private static final String DEFAULT_IMPLEMENTATION_CLASS = "com.ebizance.tdsampler.stack.impl.DefaultThreadDumpParser";

	String path;
	boolean countDuplicatedMethods = false;
	String includeListThreadName = "";
	String includeListThreadState = "";
	String includeListMethod = "";
	String excludeListMethod = "";
	String includeListThread = "";
	String excludeListThread = "";
	String includeListIOWait = "";
	String implementationClass = DEFAULT_IMPLEMENTATION_CLASS;

	public TDSamplerContext()
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
	public boolean isCountDuplicatedMethods()
	{
		return countDuplicatedMethods;
	}

	/**
	 * @param countDuplicateMethods
	 *           the countDuplicateMethods to set
	 */
	public void setCountDuplicatedMethods(final boolean countDuplicateMethods)
	{
		this.countDuplicatedMethods = countDuplicateMethods;
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
	public Class getImplementationClass()
	{
		Class classImplementation = null;
		try {
			classImplementation = Class.forName(implementationClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classImplementation;
	}

	/**
	 * @param implementationClass
	 *           the implementationClass to set
	 */
	public void setImplementationClass(String implementationClass)
	{
		this.implementationClass = implementationClass;
	}
	
	public ThreadDumpParser getThreadDumpParser()
	{
		final Class partypes[] = new Class[0];
		Constructor ct = null;
		try
		{
			ct = getImplementationClass().getConstructor(partypes);
		}
		catch (final SecurityException e2)
		{
			e2.printStackTrace();
		}
		catch (final NoSuchMethodException e2)
		{
			e2.printStackTrace();
		}
		ThreadDumpParser threadDumpParser = null;
		try
		{
			threadDumpParser = (ThreadDumpParser) ct.newInstance();
		}
		catch (final IllegalArgumentException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (final InstantiationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (final IllegalAccessException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (final InvocationTargetException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return threadDumpParser;
	}

}