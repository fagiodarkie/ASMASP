package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import org.objectweb.asm.Type;


public class NotExpression extends IBoolExpression {
	
	public NotExpression(Type t, IBoolExpression exp) {
		super(t, exp);
	}
	
	@Override
	public boolean evaluate() {
		return !leftB.evaluate();
	}	

}
