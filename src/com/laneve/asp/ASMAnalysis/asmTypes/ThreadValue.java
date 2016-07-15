package com.laneve.asp.ASMAnalysis.asmTypes;

public class ThreadValue extends AnValue {

	protected long ID;
	public static final String fullyQualifiedName = "java.lang.Thread";
	
	public ThreadValue(AnValue a, long ID) {
		super(a);
		this.ID = ID;
	}
	
	public ThreadValue clone() {
		return new ThreadValue(new AnValue(type), ID);
	}

	public long getID() {
		return ID;
	}

}
