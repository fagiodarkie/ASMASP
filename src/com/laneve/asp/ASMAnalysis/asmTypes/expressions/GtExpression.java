package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class GtExpression extends IBoolExpression {
	
protected IExpression leftExp, rightExp;
	
	public GtExpression(Type t, IExpression left, IExpression right) {
		super(t);
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public boolean evaluate() {
		
		if (leftExp.evaluate() > rightExp.evaluate()) {
			return true;
		}
		else return false; 
	}

}
