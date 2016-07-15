package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class NeExpression implements IBoolExpression {
	
protected IBoolExpression leftExp, rightExp;
	
	public NeExpression(IBoolExpression left, IBoolExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() != rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
