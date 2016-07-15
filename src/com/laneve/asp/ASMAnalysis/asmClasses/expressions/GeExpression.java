package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class GeExpression implements IBoolExpression {
	
protected IExpression leftExp, rightExp;
	
	public GeExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() >= rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}