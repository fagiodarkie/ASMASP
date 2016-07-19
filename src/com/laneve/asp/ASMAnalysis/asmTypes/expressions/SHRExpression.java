package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class SHRExpression extends IExpression {

	public SHRExpression(Type t, IExpression e1, IExpression e2) {
		super(t, e1, e2);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() >> (right.evaluate() & 0x1f);
	}

}
