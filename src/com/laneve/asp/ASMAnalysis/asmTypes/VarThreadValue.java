package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;

public class VarThreadValue extends AbstractThread {

	protected long index;
	protected AnalysisContext context;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public VarThreadValue(AnValue a, long ID, AnalysisContext c) {
		super(a);
		this.index = ID;
		context = c;
	}
	
	public VarThreadValue clone() {
		return new VarThreadValue(new AnValue(type), index, context);
	}

	@Override
	public boolean equalValue(AnValue other) {
		return index == ((VarThreadValue)other).index;
	}

	@Override
	public String toString() {
		return "x" + index;
	}

	
	public long getIndex() {
		return index;
	}

	

}
