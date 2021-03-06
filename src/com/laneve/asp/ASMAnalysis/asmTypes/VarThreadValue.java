package com.laneve.asp.ASMAnalysis.asmTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.utils.Names;

public class VarThreadValue extends ThreadValue {

	protected int index;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public VarThreadValue(AnValue a, long ID, int stat, String c2, int ind) {
		super(a, ID, stat, true, c2);
		this.index = ind;
	}
	
	public VarThreadValue clone() {
		VarThreadValue x = new VarThreadValue(this, threadID, status, name, index);
		return x;
	}

	@Override
	public boolean equalValue(AnValue other) {
		if (!(other instanceof VarThreadValue))
			return false;
		return index == ((VarThreadValue)other).index;
	}

	public ThreadValue compute(List<AnValue> parameters) {
		
		String n = name;
		if (n.split("\\.").length > 1)
			index = Names.getPos(n.substring(0, n.indexOf('.')));
			
		if (index >= parameters.size())
			return this;
		
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
