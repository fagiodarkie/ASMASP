package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

public class USHRExpression extends IExpression {

	public USHRExpression(Type t, IExpression v1, IExpression v2) {
		super(t, v1, v2);
	}

	private IExpression right, left;
	
	public Long evaluate() {
		return left.evaluate() >> (right.evaluate() & 0x3f);
	}

	@Override
	public IExpression clone() {
		return new USHRExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " >> " + right.toString() + ")";
	}

	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof USHRExpression)) return false;
		return right.equalExpression(iExpression.right) && left.equalExpression(iExpression.left);
	}

}
