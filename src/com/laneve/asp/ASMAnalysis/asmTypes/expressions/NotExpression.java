package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class NotExpression extends IBoolExpression {
	
protected IBoolExpression Exp;
	
	public NotExpression(Type t, IBoolExpression exp) {
		super(t);
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
