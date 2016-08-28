package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;

public class ThreadValue extends AbstractThread {

	protected long threadID;
	protected AnalysisContext context;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public ThreadValue(AnValue a, long ID, AnalysisContext c, boolean variable, String c2) {
		super(a);
		this.threadID = ID;
		this.isVariable = variable;
		context = c;
		name = c2;
	}
	
	public ThreadValue clone() {
		return new ThreadValue(this, threadID, context, isVariable, name);
	}

	@Override
	public boolean equalValue(AnValue other) {
		return context.getStatusOfThread(threadID) == context.getStatusOfThread(((ThreadValue)other).threadID);
	}
	
	public long getThreadID() {
		return threadID;
	}

	public String getVariableName() {
		if (!isVariable) {
			// ?
		}
		return name;
	}
	
	@Override
	public String toString() {
		return (isVariable ? getVariableName() : "t" + threadID);
	}
	
	public String printValue() {
		int status = context.getStatusOfThread(ID);
		return "" + status;
	}

	public boolean equalThread(ThreadValue x) {
		return equalValue(x) && x.ID == ID && name.equalsIgnoreCase(x.name);
	}
	

}
