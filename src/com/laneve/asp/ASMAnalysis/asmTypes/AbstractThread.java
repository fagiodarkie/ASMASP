package com.laneve.asp.ASMAnalysis.asmTypes;

public abstract class AbstractThread extends AnValue {

	public AbstractThread(AnValue a) {
		super(a);
		className = "java.lang.Thread";
	}
	
	@Override
	public abstract String toString();
}
