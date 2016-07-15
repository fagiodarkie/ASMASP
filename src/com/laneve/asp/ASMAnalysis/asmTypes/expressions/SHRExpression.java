package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class SHRExpression extends IExpression {

	private IExpression leftExp, rightExp;

	public SHRExpression(Type t, IExpression e1, IExpression e2) {
		super(t);
		leftExp = e1;
		rightExp = e2;
	}
	
	@Override
	public Long evaluate() {
		Long res = leftExp.evaluate();
		for (long i = 0; i < rightExp.evaluate(); ++i)
			res /= 2;
		return res;
	}

}
