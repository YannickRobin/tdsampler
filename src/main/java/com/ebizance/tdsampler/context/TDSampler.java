package com.ebizance.tdsampler.context;

public class TDSampler {

	private static final ThreadLocal<TDSamplerContext> context_ = new ThreadLocal<TDSamplerContext>()
	{
		@Override
		protected TDSamplerContext initialValue()
		{
			return new TDSamplerContext();
		}
	};
	
	public static void setContext(TDSamplerContext context)
	{
		context_.set(context);
	}
	
	public static TDSamplerContext getContext()
	{
		return context_.get();
	}
	
}
