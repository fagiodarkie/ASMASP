package com.laneve.asp.ASMAnalysis.bTypes;

import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;

public class ThreadResource implements IBehaviour {

	public static final int ALLOCATED = 0, ACQUIRE = 1, ALREADY_ACQUIRED = 2, RELEASE = 3, ALREADY_RELEASED = 4, DELTA = 5; 
	
	protected ThreadValue thread;
	protected int status;
	
	public ThreadResource(ThreadValue t, int status) {
		thread = t;
		this.status = status;
	}
	
	public ThreadValue getThreadValue() {
		return thread;
	}
	
	public String toString() {
		switch (status) {
		case ACQUIRE: return thread.toString() + ".acquire";
		case RELEASE: return thread.toString() + ".release";
		default: return "";
		}
	}

	public boolean isRelease() {
		return status == Atom.RELEASE;
	}
	
	public ThreadResource clone() {
		return new ThreadResource(thread, status);
	}
	
	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		return (updatedBehaviour instanceof ThreadResource) && (status == ((ThreadResource)updatedBehaviour).status);
	}

	@Override
	public boolean equal(IBehaviour o) {
		return equalBehaviour(o) && thread.toString().equalsIgnoreCase(((ThreadResource)o).thread.toString());
	}
	
	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		if (frameBehaviour instanceof ThreadResource)
			status = ((ThreadResource)frameBehaviour).status;
	}
	
}
