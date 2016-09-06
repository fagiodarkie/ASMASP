package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class UnknownExpression extends IExpression {

	public UnknownExpression(Type t) {
		super(t);
	}
	
	public UnknownExpression() {
		this(Type.INT_TYPE);
	}
	

	@Override
	public Long evaluate() {
		return null;
	}

	@Override
	public String toString() {
		return ((AnValue)this).toString() + ":-";
	}

	@Override
	public IExpression cloneExpression() {
		return new UnknownExpression();
	}

	@Override
	public boolean equalValue(AnValue other) {
		return other instanceof UnknownExpression;
	}

}
