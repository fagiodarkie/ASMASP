package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class SumExpression extends IExpression {

	public SumExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() + right.evaluate();
	}

	@Override
	public IExpression cloneExpression() {
		return new SumExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " + " + right.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof SumExpression)) return false;
		return (right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left))
				|| (right.equalValue(((IExpression)iExpression).left) && left.equalValue(((IExpression)iExpression).right));
	}

}
