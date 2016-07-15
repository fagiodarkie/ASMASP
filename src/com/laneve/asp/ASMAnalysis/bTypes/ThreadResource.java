package com.laneve.asp.ASMAnalysis.bTypes;

public class ThreadResource {

	public static final int ACQUIRE = 0, RELEASE = 1, ALREADY_ACQUIRED = 2, ALREADY_RELEASED = 3;

	protected long ID;
	protected int status;
	
	public ThreadResource(long id, int status) {
		ID = id;
		this.status = status;
	}
	
	public String toString() {
		switch (status) {
		case ACQUIRE: return "t" + ID + ".acquire";
		case RELEASE: return "t" + ID + ".release";
		default: return "";
		}
	}
	
}
