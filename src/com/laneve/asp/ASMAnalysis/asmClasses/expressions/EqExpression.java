package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class EqExpression implements IBoolExpression {
	
	protected IBoolExpression leftExp, rightExp;
	
	public EqExpression(IBoolExpression left, IBoolExpression right) {
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
