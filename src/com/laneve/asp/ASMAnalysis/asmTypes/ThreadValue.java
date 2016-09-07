package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;

public class ThreadValue extends AbstractThread {

	protected long threadID;
	protected int status;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public ThreadValue(AnValue a, long ID, int c, boolean variable, String c2) {
		super(a);
		this.threadID = ID;
		this.isVariable = variable;
		status = c;
		name = c2;
	}
	
	public ThreadValue clone() {
		ThreadValue t = new ThreadValue(this, threadID, status, isVariable, name);
		return t;
	}

	@Override
	public boolean equalValue(AnValue other) {
		return status == ((ThreadValue)other).status;
	}
	
	public long getThreadID() {
		return threadID;
	}

	public int getStatus() {
		//return context.getStatusOfThread(threadID);
		return status;
	}
	
	public void initThread() {
		status = ThreadResource.ALLOCATED;
	}
	
	public void runThread() {
		status = ThreadResource.ALREADY_ACQUIRED;
	}
	
	public void joinThread() {
		status = ThreadResource.ALREADY_RELEASED;
	}
	
	public void cloneStatus(ThreadValue t) {
		status = t.status;
	}
	
	public void setStatus(int s) {
		status = s;
	}
	
	public String getVariableName() {
		if (!isVariable) {
			// ?
		}
		return name;
	}
	
	@Override
	public String toString() {
		return (isVariable ? getVariableName() : "t" + threadID) + ":" + getStatus();
	}
	
	public String printValue() {
		return Integer.toString(status);
	}

	public boolean equalThread(ThreadValue x) {
		return equalValue(x) && x.ID == ID && name.equalsIgnoreCase(x.name);
	}


	public void setStatus(Integer value) {
		status = value;
	}
	

}
