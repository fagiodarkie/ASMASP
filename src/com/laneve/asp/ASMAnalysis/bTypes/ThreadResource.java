package com.laneve.asp.ASMAnalysis.bTypes;

import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;

public class ThreadResource implements IBehaviour {

	public static final int ACQUIRE = 0, RELEASE = 1, ALREADY_ACQUIRED = 2,
			ALREADY_RELEASED = 3, MAYBE_RELEASED = 4, ALLOCATED = 5, DELTA = 6;

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
		case MAYBE_RELEASED: return thread.toString() + ".released?";
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
