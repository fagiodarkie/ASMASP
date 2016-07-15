package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class TrueExpression extends IBoolExpression {
	
	public TrueExpression(Type t) {
		super(t);
	}
	
	@Override
	public boolean evaluate() {
		
		return true;
	}

}
