package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class DivExpression extends IExpression {
	
	public DivExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		if (right.evaluate() != 0){
			return left.evaluate() / right.evaluate();
		}
		else {
			throw new Error("Division by zero detected.");
		}
	}

	@Override
	public IExpression clone() {
		return new DivExpression(type, left.clone(), right.clone());
	}

}