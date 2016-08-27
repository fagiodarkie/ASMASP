package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class MulExpression extends IExpression {
	
	public MulExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() * right.evaluate();
	}

	@Override
	public IExpression cloneExpression() {
		return new MulExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " * " + right.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof MulExpression)) return false;
		return right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left)
				|| (left.equalValue(((IExpression)iExpression).right) && right.equalValue(((IExpression)iExpression).left));
	}

}
