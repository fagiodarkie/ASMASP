package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class EqExpression implements IBoolExpression {
	
	protected IExpression leftExp, rightExp;
	
	public EqExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() == rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
