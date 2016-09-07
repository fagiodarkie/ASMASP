package com.laneve.asp.ASMAnalysis.bTypes;

public class Atom implements IBehaviour {

	public static int RETURN = 0;
	
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
		
	}

}
