package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class IntAndExpression extends IExpression {

	public IntAndExpression(Type t, IExpression l, IExpression r) {
		super(t, l, r);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() & right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new IntAndExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " & " + right.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof IntAndExpression)) return false;
		return right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left)
				|| (left.equalValue(((IExpression)iExpression).right) && right.equalValue(((IExpression)iExpression).left));
	}

}
