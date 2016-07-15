package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class FalseExpression extends IBoolExpression {
	
	public FalseExpression(Type t, IBoolExpression exp) {
		super(t);
	}
	
	@Override
	public boolean evaluate() {
		
		return false; 
	}

}
