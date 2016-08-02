package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;

public class ThreadValue extends AbstractThread {

	protected long ID;
	protected AnalysisContext context;
	protected boolean variable;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public ThreadValue(AnValue a, long ID, AnalysisContext c, boolean variable) {
		super(a);
		this.ID = ID;
		this.variable = variable;
		context = c;
	}
	
	public ThreadValue clone() {
		return new ThreadValue(new AnValue(type), ID, context, variable);
	}

	@Override
	public boolean equalValue(AnValue other) {
		return context.getStatusOfThread(ID) == context.getStatusOfThread(((ThreadValue)other).ID);
	}

	public long getID() {
		return ID;
	}

	@Override
	public String toString() {
		return (variable ? "x" : "t") + ID;
	}


	public boolean isVariable() {
		return variable;
	}
	

}
