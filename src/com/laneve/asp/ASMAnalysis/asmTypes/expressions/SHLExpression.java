package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class SHLExpression extends IExpression {

	public SHLExpression(Type t, IExpression e1, IExpression e2) {
		super(t, e1, e2);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() << (right.evaluate() & 0x1f);
	}

	@Override
	public IExpression clone() {
		return new SHLExpression(type, left.clone(), right.clone());
	}

	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof SHLExpression)) return false;
		return right.equalExpression(iExpression.right) && left.equalExpression(iExpression.left);
	}

}
