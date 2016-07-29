package com.laneve.asp.ASMAnalysis.bTypes;

public class ConcatBehaviour implements IBehaviour {

	protected IBehaviour left, right;
	
	public ConcatBehaviour(IBehaviour l, IBehaviour r) {
		left = l; right = r;
	}
	
	public String toString() {
		return left.toString() + "; " + right.toString();
	}
	
	public ConcatBehaviour clone() {
		return new ConcatBehaviour(left.clone(), right.clone());
	}
	
	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		if (!(updatedBehaviour instanceof ConcatBehaviour))
			return false;
		ConcatBehaviour b = (ConcatBehaviour)updatedBehaviour;
		
		return left.equalBehaviour(b.left) && right.equalBehaviour(b.right);
	}

	public boolean equal(IBehaviour o) {
		return equalBehaviour(o) && left.equal(((ConcatBehaviour)o).left) && right.equal(((ConcatBehaviour)o).right);
	}

	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		try {
			ConcatBehaviour c = (ConcatBehaviour) frameBehaviour;
			left.mergeWith(c.left);
			right.mergeWith(c.right);
		} catch (Throwable t) {
			
		}
		
	}

}
