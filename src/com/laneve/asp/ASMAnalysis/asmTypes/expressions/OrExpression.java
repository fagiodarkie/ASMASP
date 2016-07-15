package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class OrExpression implements IBoolExpression {
	
protected IBoolExpression leftExp, rightExp;
	
	public OrExpression(IBoolExpression left, IBoolExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() || rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
