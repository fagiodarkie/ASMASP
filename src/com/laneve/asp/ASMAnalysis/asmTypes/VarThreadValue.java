package com.laneve.asp.ASMAnalysis.asmTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;
import com.laneve.asp.ASMAnalysis.utils.Names;

public class VarThreadValue extends ThreadValue {

	protected int index;
	protected AnalysisContext context;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public VarThreadValue(AnValue a, long ID, AnalysisContext c, String c2, int ind) {
		super(a, ID, c, true, c2);
		this.index = ind;
	}
	
	public VarThreadValue clone() {
		return new VarThreadValue(this, threadID, context, name, index);
	}

	@Override
	public boolean equalValue(AnValue other) {
		return index == ((VarThreadValue)other).index;
	}

	public ThreadValue compute(List<AnValue> parameters) {
		
		if (index >= parameters.size())
			return this;
		
		String n = name;
		if (n.split("\\.").length == 1)
			return (ThreadValue)parameters.get(index);
		else return (ThreadValue)parameters.get(index).getField(n.substring(n.indexOf('.') + 1));
	}
	
	@Override
	public String toString() {
		String n = name;
		if (n.split("\\.").length == 1) 
			return Names.get(index);
		return Names.get((int) index) + "." + n.substring(n.indexOf('.') + 1);
	}

	public long getIndex() {
		return index;
	}

	

}
