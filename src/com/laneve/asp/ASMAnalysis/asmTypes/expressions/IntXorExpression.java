package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class IntXorExpression extends IExpression {

	public IntXorExpression(Type t, IExpression l, IExpression r) {
		super(t, l, r);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() ^ right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new IntXorExpression(type, left.clone(), right.clone());
	}

}
