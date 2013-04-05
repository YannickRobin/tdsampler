package com.ebizance.tdsampler.context;

public class TDSampler {

	private static final ThreadLocal<TDSamplerContext> context = new ThreadLocal<TDSamplerContext>()
	{
		@Override
		protected TDSamplerContext initialValue()
		{
			return new TDSamplerContext();
		}
	};
	
	
	public static TDSamplerContext getContext()
	{
		return context.get();
	}
	
}
