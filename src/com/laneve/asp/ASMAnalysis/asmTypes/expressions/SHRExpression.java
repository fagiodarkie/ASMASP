package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class SHRExpression extends IExpression {

	public SHRExpression(Type t, IExpression e1, IExpression e2) {
		super(t, e1, e2);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() >> (right.evaluate() & 0x1f);
	}

	@Override
	public IExpression clone() {
		return new SHRExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " >> " + right.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof SHRExpression)) return false;
		return right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left);
	}

}
