package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class RemExpression extends IExpression {
	
	public RemExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() % right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new RemExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " % " + right.toString() + ")";
	}
	
	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof RemExpression)) return false;
		return right.equalExpression(iExpression.right) && left.equalExpression(iExpression.left);
	}

}