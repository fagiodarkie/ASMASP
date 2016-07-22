package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class SubExpression extends IExpression {
	
	public SubExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() - right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new SubExpression(type, left.clone(), right.clone());
	}

}
