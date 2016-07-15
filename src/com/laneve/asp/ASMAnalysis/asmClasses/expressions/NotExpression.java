package com.laneve.asp.ASMAnalysis.asmClasses.expressions;

public class NotExpression implements IBoolExpression {
	
protected IBoolExpression Exp;
	
	public NotExpression(IBoolExpression exp) {
		Exp = exp;
	}
	
	@Override
	public boolean evaluate() {
		
		if (!Exp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
