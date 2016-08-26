package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;

public class ThreadValue extends AbstractThread {

	protected long ID;
	protected AnalysisContext context;
	protected boolean variable;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public ThreadValue(AnValue a, long ID, AnalysisContext c, boolean variable, String c2) {
		super(a);
		this.ID = ID;
		this.variable = variable;
		context = c;
		name = c2;
	}
	
	public ThreadValue clone() {
		return new ThreadValue(new AnValue(type), ID, context, variable, name);
	}

	@Override
	public boolean equalValue(AnValue other) {
		return context.getStatusOfThread(ID) == context.getStatusOfThread(((ThreadValue)other).ID);
	}
	
	public long getID() {
		return ID;
	}

	public String getVariableName() {
		if (!variable) {
			// ?
		}
		return name;
	}
	
	@Override
	public String toString() {
		return (variable ? getVariableName() : "t" + ID);
	}
	
	public String printValue() {
		int status = context.getStatusOfThread(ID);
		return "" + status;
	}

	public boolean isVariable() {
		return variable;
	}


	public boolean equalThread(ThreadValue x) {
		return x.variable == variable && x.ID == ID && name.equalsIgnoreCase(x.name);
	}
	

}
