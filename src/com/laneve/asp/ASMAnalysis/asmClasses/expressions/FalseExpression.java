package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class FalseExpression implements IBoolExpression {
	
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
