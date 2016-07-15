package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class TrueExpression extends IBoolExpression {
	
protected IBoolExpression Exp;
	
	public TrueExpression(Type t, IBoolExpression exp) {
		super(t);
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
