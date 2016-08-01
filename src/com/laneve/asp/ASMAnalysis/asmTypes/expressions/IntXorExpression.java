package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class IntXorExpression extends IExpression {

	public IntXorExpression(Type t, IExpression l, IExpression r) {
		super(t, l, r);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() ^ right.evaluate();
	}

	public String toString() {
		return "(" + left.toString() + " ^ " + right.toString() + ")";
	}

	@Override
	public IExpression clone() {
		return new IntXorExpression(type, left.clone(), right.clone());
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof IntXorExpression)) return false;
		return right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left)
				|| (left.equalValue(((IExpression)iExpression).right) && right.equalValue(((IExpression)iExpression).left));
	}

}
