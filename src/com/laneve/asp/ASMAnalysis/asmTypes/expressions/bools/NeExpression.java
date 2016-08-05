package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;


public class NeExpression extends IBoolExpression {
	
	public NeExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public boolean evaluate() {
		return (left.evaluate() != right.evaluate());
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " != " + right.toString() + ")";
	}

}
