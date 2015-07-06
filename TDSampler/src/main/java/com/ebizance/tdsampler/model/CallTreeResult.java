/**
 *
 */
package com.ebizance.tdsampler.model;

import com.ebizance.tdsampler.service.TDSamplerResult;


/**
 * @author i304002
 *
 */
public class CallTreeResult
{
	private String jsonTree;
	private TDSamplerResult result;

	/**
	 * @return the jsonTree
	 */
	public String getJsonTree()
	{
		return jsonTree;
	}

	/**
	 * @param jsonTree
	 *           the jsonTree to set
	 */
	public void setJsonTree(final String jsonTree)
	{
		this.jsonTree = jsonTree;
	}

	/**
	 * @return the result
	 */
	public TDSamplerResult getResult()
	{
		return result;
	}

	/**
	 * @param result
	 *           the result to set
	 */
	public void setResult(final TDSamplerResult result)
	{
		this.result = result;
	}
}
