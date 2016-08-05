package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import org.objectweb.asm.Type;

public class TrueExpression extends IBoolExpression {
	
	public TrueExpression(Type t) {
		super(t);
	}
	
	@Override
	public boolean evaluate() {
		
		return true;
	}

	@Override
	public boolean equal(IBoolExpression o) {
		return o instanceof TrueExpression;
	}
	
	@Override
	public String toString() {
		return "true";
	}
}
