package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class SubExpression implements IExpression {
	
protected IExpression leftExp, rightExp;
	
	public SubExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public Long evaluate() {
		return leftExp.evaluate() - rightExp.evaluate();
	}

}
