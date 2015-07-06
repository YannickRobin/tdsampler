package com.ebizance.tdsampler.model;

import java.util.Set;
import java.util.TreeSet;

public class MethodNode implements Comparable<MethodNode>{

	private String method;
	private int count;
	private Set<MethodNode> subMethodNodes = new TreeSet<MethodNode>();
	private MethodNode parentNode;
	
	public MethodNode() {
		// empty
	}

	public MethodNode(String method, int count) {
		this.method = method;
		this.count = count;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void increaseCount()
	{
		this.count++;
	}

	public void increaseCount(int cnt)
	{
		this.count = this.count + cnt;
	}
	
	public Set<MethodNode> getSubMethodNodes() {
		return subMethodNodes;
	}

	public void setSubMethodNodes(Set<MethodNode> subMethodNodes) {
		this.subMethodNodes = subMethodNodes;
	}
	
	/**
	 * @return the parentNode
	 */
	public MethodNode getParentNode() {
		return parentNode;
	}

	/**
	 * @param parentNode the parentNode to set
	 */
	public void setParentNode(MethodNode parentNode) {
		this.parentNode = parentNode;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
		return result;
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		MethodNode other = (MethodNode) obj;
//		if (method == null) {
//			if (other.method != null)
//				return false;
//		} else if (!method.equals(other.method))
//			return false;
//		if (parentNode == null) {
//			if (other.parentNode != null)
//				return false;
//		} else if (!parentNode.equals(other.parentNode))
//			return false;
//		return true;
//	}

	@Override
	public int compareTo(MethodNode o) {
		if (equals(o))
		{
			return 0;
		}
		else if (o.count == this.count )
		{
			return -1;
		}
		else
		{
			return o.count - this.count;
		}
	}
	
	@Override
	public String toString() {
		return "method = " + method + " count = " + count;
	}
}
