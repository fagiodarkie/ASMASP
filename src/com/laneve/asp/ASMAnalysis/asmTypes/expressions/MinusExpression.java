package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class MinusExpression extends IExpression {
	
	public MinusExpression(Type t, IExpression exp) {
		super(t, exp);
	}
	
	@Override
	public Long evaluate() {
		return - left.evaluate();
	}

	@Override
	public IExpression clone() {
		return new MinusExpression(type, left.clone());
	}

	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof MinusExpression)) return false;
		return left.equalExpression(iExpression.left);
	}

}
