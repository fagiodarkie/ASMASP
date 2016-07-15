package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class OrExpression extends IBoolExpression {
	
protected IBoolExpression leftExp, rightExp;
	
	public OrExpression(Type t, IBoolExpression left, IBoolExpression right) {
		super(t);
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() || rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
