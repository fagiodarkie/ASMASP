package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class SumExpression implements IExpression {

	protected IExpression leftExp, rightExp;
	
	public SumExpression(IExpression left, IExpression right) {
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public Long evaluate() {
		return leftExp.evaluate() + rightExp.evaluate();
	}

}
