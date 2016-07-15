package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class GtExpression implements IBoolExpression {
	
protected IExpression leftExp, rightExp;
	
	public GtExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() > rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
