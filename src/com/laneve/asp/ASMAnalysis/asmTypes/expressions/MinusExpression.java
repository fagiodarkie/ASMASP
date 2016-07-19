package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class MinusExpression extends IExpression {
	
	public MinusExpression(Type t, IExpression exp) {
		super(t, exp);
	}
	
	@Override
	public Long evaluate() {
		return - left.evaluate();
	}

}
