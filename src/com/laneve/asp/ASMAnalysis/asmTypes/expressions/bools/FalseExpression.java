package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import org.objectweb.asm.Type;

public class FalseExpression extends IBoolExpression {
	
	public FalseExpression(Type t) {
		super(t);
	}
	
	@Override
	public boolean evaluate() {
		
		return false; 
	}

}
