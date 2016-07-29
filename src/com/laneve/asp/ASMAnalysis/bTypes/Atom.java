package com.laneve.asp.ASMAnalysis.bTypes;

public class Atom implements IBehaviour {

	public static int ACQUIRE = 0, RELEASE = 1, RETURN = 2;
	
	protected static final String acq = ":acquired", rel = ":released";
	
	protected static int released = 0, acquired = 0;
	
	protected static int getNextReleased() {
		return released++;
	}
	
	protected static int getNextAcquired() {
		return acquired++;
	}

	private int type;
	
	public Atom(int type) {
		this.type = type;
	}
	
	public Atom clone() {
		return new Atom(type);
	}
	
	public String toString() {
		return "0";
	}

	public boolean equal(IBehaviour o) {
		return equalBehaviour(o);
	}

	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		if (! (updatedBehaviour instanceof Atom))
			return false;
		return type == ((Atom)updatedBehaviour).type;
	}

	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		// TODO Auto-generated method stub
		
	}

}
