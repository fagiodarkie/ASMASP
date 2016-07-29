package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class SumExpression extends IExpression {

	public SumExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() + right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new SumExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " + " + right.toString() + ")";
	}

	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof SumExpression)) return false;
		return (right.equalExpression(iExpression.right) && left.equalExpression(iExpression.left))
				|| (right.equalExpression(iExpression.left) && left.equalExpression(iExpression.right));
	}

}
