package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class FalseExpression extends IBoolExpression {
	
protected IBoolExpression Exp;
	
	public FalseExpression(IBoolExpression exp) {
		Exp = exp;
	}
	
	@Override
	public boolean evaluate() {
		
		if (Exp.evaluate() == false) {
			return true;
		}
		else return false; 
	}

}
