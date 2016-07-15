package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class FalseExpression extends IBoolExpression {
	
protected IBoolExpression Exp;
	
	public FalseExpression(Type t, IBoolExpression exp) {
		super(t);
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
