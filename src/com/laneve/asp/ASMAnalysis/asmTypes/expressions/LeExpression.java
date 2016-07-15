package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class LeExpression implements IBoolExpression {
	
protected IExpression leftExp, rightExp;
	
	public LeExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() <= rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
