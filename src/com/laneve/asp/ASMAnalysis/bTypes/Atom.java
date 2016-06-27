package com.laneve.asp.ASMAnalysis.bTypes;

public class Atom implements IBehaviour {

	public static int ACQUIRE = 0, RELEASE = 1, RETURN = 2;
	
	protected static final String acq = "acquired", rel = ":released";
	
	protected static int released = 0, acquired = 0;
	
	protected static int getNextReleased() {
		return released++;
	}
	
	protected static int getNextAcquired() {
		return acquired++;
	}

	private int ID, type;
	
	public Atom(int type) {
		this.type = type;
		if (type == RELEASE)
			ID = getNextReleased();
		else if (type == ACQUIRE)
			ID = getNextAcquired();
		else
			ID = 0;
	}

	/*
	 * this way nobody can define their own ID, save from us.
	 */
	protected Atom(int type, int ID) {
		this.type = type;
		this.ID = ID;
	}
	
	public Atom clone() {
		return new Atom(type, ID);
	}
	
	@Override
	public String printBehaviour() {
		if (type == RETURN)
			return "0";
		return ID + (type == ACQUIRE ? acq : rel);
	}

}
