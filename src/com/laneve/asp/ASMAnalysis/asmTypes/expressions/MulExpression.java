package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class MulExpression implements IExpression {
	
protected IExpression leftExp, rightExp;
	
	public MulExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public Long evaluate() {
		return leftExp.evaluate() * rightExp.evaluate();
	}

}
