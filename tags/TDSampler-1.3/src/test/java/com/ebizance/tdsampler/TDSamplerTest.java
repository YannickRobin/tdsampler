package com.ebizance.tdsampler;

import org.junit.Test;

import com.ebizance.tdsampler.TDSampler;

public class TDSamplerTest {

	/**
	 * Add the following environment variable to TDSamplerTest Junit test configuration:
	 * tdsampler.configuration=src/main/conf/conf.properties
	 * log4j.configuration=src/main/conf/log4j.properties
	 */
	public void mainTest() throws Exception
	{
		String[] args = new String[1];
		//args[0] = "/Users/yannick/Documents/projects/Hybris/GL/test05-30-1";
		args[0] = "/Users/yannick/Documents/projects/Hybris/GL/test05-30-3";
		TDSampler.main(args);
	}
	
}