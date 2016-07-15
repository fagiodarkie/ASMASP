package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public abstract class IExpression extends AnValue{
	
	public IExpression(Type t) {
		super(t);
	}

	public abstract Long evaluate();

}
