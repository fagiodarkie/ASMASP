package com.laneve.asp.ASMAnalysis.bTypes;

public interface IBehaviour {

	public abstract boolean equalBehaviour(IBehaviour updatedBehaviour);

	public abstract boolean equal(IBehaviour o);
	
	public abstract IBehaviour clone();

	public abstract void mergeWith(IBehaviour frameBehaviour);
	
}
