package com.laneve.asp.ASMAnalysis.bTypes;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.IBoolExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.TrueExpression;

public class ConditionalJump implements IBehaviour {

	protected int jumpStart, thenTarget, elseTarget;
	protected IBoolExpression thenCondition, elseCondition;
	protected IBehaviour thenBranch, elseBranch;
	
	public ConditionalJump(int jumpStart2, IBoolExpression thenCondition2,
			int thenTarget2, IBoolExpression elseCondition2, int elseTarget2) {
		jumpStart = jumpStart2;
		thenCondition = thenCondition2;
		thenTarget = thenTarget2;
		elseCondition = elseCondition2;
		elseTarget = elseTarget2;
		thenBranch = elseBranch = null;
	}
	
	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		if (updatedBehaviour instanceof ConditionalJump) {
			ConditionalJump o = (ConditionalJump)updatedBehaviour;
			return jumpStart == o.jumpStart
					&& elseTarget == o.elseTarget
					&& thenTarget == o.thenTarget;
		}
		return false;
	}

	@Override
	public boolean equal(IBehaviour other) {
		if (equalBehaviour(other)) {
			ConditionalJump o = (ConditionalJump)other;
			if (thenCondition != null)
				return (thenCondition.equal(o.thenCondition));
		}
		return false;
	}

	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		// TODO
	}

	public int getJumpStart() {
		return jumpStart;
	}
	
	public int getThenIndex() {
		return thenTarget;
	}
	public int getElseIndex() {
		return elseTarget;
	}
	
	public void setBranches(IBehaviour thenB, IBehaviour elseB) {
		thenBranch = thenB;
		elseBranch = elseB;
	}

	public IBehaviour clone() {
		return new ConditionalJump(jumpStart, thenCondition, thenTarget, elseCondition, elseTarget);
	}
	
	public IBehaviour getThenBranch() {
		return thenBranch;
	}
	public IBehaviour getElseBranch() {
		return elseBranch;
	}
	
	@Override
	public String toString() {
		if (thenBranch == null && null == elseBranch) {
			return "";
		} else if (thenBranch == null) {
			return (elseCondition == null ? "" : "[" + elseCondition.toString() + "]") 
					+ elseBranch.toString();
		} else if (elseBranch == null) {
			return (thenCondition == null ? "" : "[" + thenCondition.toString() + "]")
					+ thenBranch.toString();
		}
		
		if ((thenCondition == null && elseCondition == null)
				|| (thenCondition instanceof TrueExpression && elseCondition instanceof TrueExpression))
			return "{" + thenBranch.toString() + " + " + elseBranch.toString() + "}";
		
		return "[" + thenCondition.toString() + "]{" + thenBranch.toString() +
				"} + [" + elseCondition.toString() + "]{" + elseBranch.toString() + "}";
	}
}
