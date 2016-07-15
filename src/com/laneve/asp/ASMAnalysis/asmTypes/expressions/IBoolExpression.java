package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public abstract class IBoolExpression extends AnValue {
	
	public IBoolExpression(Type t) {
		super(Type.BOOLEAN_TYPE);
	}

	public abstract boolean evaluate();

}
