package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class MinusExpression implements IExpression {
	
protected IExpression Exp;
	
	public MinusExpression(IExpression exp) {
		Exp = exp;
	}
	
	@Override
	public Long evaluate() {
		return - Exp.evaluate();
	}

}
