package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;

public class ThreadValue extends AnValue {

	protected long ID;
	protected AnalysisContext context;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public ThreadValue(AnValue a, long ID, AnalysisContext c) {
		super(a);
		this.ID = ID;
		context = c;
	}
	
	public ThreadValue clone() {
		return new ThreadValue(new AnValue(type), ID, context);
	}

	@Override
	public boolean equalValue(AnValue other) {
		return context.getStatusOfThread(ID) == context.getStatusOfThread(((ThreadValue)other).ID);
	}
	
	public long getID() {
		return ID;
	}

}
