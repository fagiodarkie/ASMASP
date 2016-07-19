package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import org.objectweb.asm.Type;


public class NeExpression extends IBoolExpression {
	
	public NeExpression(Type t, IBoolExpression left, IBoolExpression right) {
		super(t, left, right);
	}
	
	@Override
	public boolean evaluate() {
		return (left.evaluate() != right.evaluate());
	}

}
