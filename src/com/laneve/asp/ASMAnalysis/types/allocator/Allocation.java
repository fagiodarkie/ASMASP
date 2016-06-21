package com.laneve.asp.ASMAnalysis.types.allocator;

import org.objectweb.asm.tree.analysis.Value;

public class Allocation implements Value {
	
	public enum Status {ACQUIRED, RELEASED, FALSE_RELEASE};
	
	protected static int maxID = 0;
	protected static int generateID() {
		return maxID++;
	}
	
	private int ID;
	private Status status;
	
	public Allocation() {
		ID = generateID();
	}
	
	public void acquire() {
		status = Status.ACQUIRED;
	}
	
	public void release() {
		status = Status.RELEASED;
	}

	public String toString() {
		return (status == Status.ACQUIRED ? ID + ":acquired"
				: (status == Status.RELEASED ? ID + ":released" : "0"));
	}
	
	public Status getStatus() {
		return status;
	}
	
	@Override
	public int getSize() {
		return 1;
	}


}
