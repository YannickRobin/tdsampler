package com.ebizance.tdsampler.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a POJO representing the current parsed thread.<br/>
 * This object is loaded into drools engine so it can be used
 * to filter threads to parse (e.g.: filter by name).
 * 
 * @author Yannick Robin
 */

public class Thread {
	private String name;
	private int state;
	private boolean isValid = true;


	private Map<String, Integer> methods = new LinkedHashMap<String, Integer>();

	public static final int STATE_RUNNABLE=0;
	public static final int STATE_WAITING=1;
	public static final int STATE_TIMED_WAITING=2;
	public static final int STATE_BLOCKED=3;
	public static final int STATE_UNKNOWN=3;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setState(String sState) {	
		if (sState.equals("RUNNABLE"))
			state=STATE_RUNNABLE;		
		else if (sState.equals("WAITING"))
			state=STATE_WAITING;		
		else if (sState.equals("TIMED_WAITING"))
			state=STATE_TIMED_WAITING;
		else if (sState.equals("BLOCKED"))
			state=STATE_BLOCKED;
		else
			state=STATE_UNKNOWN;
	}	
	
	public Map<String, Integer> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, Integer> methods) {
		this.methods = methods;
	}
}
