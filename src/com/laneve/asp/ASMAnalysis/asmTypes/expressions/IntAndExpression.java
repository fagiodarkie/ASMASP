package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class IntAndExpression extends IExpression {

	public IntAndExpression(Type t, IExpression l, IExpression r) {
		super(t, l, r);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() & right.evaluate();
	}

}
