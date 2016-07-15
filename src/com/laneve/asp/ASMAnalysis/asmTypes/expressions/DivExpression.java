package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class DivExpression extends IExpression {
	
protected IExpression leftExp, rightExp;
	
	public DivExpression(Type t, IExpression left, IExpression right) {
		super(t);
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public Long evaluate() {
		if (rightExp.evaluate() != 0){
			return leftExp.evaluate() / rightExp.evaluate();
		}
		else {
			throw new Error("Division by zero detected.");
		}
	}

}