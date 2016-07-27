package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class MulExpression extends IExpression {
	
	public MulExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() * right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new MulExpression(type, left.clone(), right.clone());
	}

	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof MulExpression)) return false;
		return right.equalExpression(iExpression.right) && left.equalExpression(iExpression.left)
				|| (left.equalExpression(iExpression.right) && right.equalExpression(iExpression.left));
	}

}
