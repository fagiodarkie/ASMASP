package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class TrueExpression implements IBoolExpression {
	
protected IBoolExpression Exp;
	
	public TrueExpression(IBoolExpression exp) {
		Exp = exp;
	}
	
	@Override
	public boolean evaluate() {
		
		if (Exp.evaluate() == true) {
			return true;
		}
		else return false; 
	}

}
