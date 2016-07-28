package com.laneve.asp.ASMAnalysis.bTypes;

public class ThreadResource implements IBehaviour {

	public static final int ACQUIRE = 0, RELEASE = 1, ALREADY_ACQUIRED = 2, ALREADY_RELEASED = 3, MAYBE_RELEASED = 4;

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
		case MAYBE_RELEASED: return "t" + ID + ".released?";
		default: return "";
		}
	}

	
	public ThreadResource clone() {
		return new ThreadResource(ID, status);
	}
	
	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		return (updatedBehaviour instanceof ThreadResource) && (status == ((ThreadResource)updatedBehaviour).status);
	}

	
	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		if (frameBehaviour instanceof ThreadResource)
			status = ((ThreadResource)frameBehaviour).status;
	}
	
}
