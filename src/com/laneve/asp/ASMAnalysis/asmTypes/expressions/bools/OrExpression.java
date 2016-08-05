package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import org.objectweb.asm.Type;

public class OrExpression extends IBoolExpression {
	
	public OrExpression(Type t, IBoolExpression left, IBoolExpression right) {
		super(t, left, right);
	}
	
	@Override
	public boolean evaluate() {
		return leftB.evaluate() || rightB.evaluate();
	}
	
	@Override
	public String toString() {
		return "(" + leftB.toString() + " || " + rightB.toString() + ")";
	}

}
