package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class MinusExpression extends IExpression {
	
protected IExpression Exp;
	
	public MinusExpression(Type t, IExpression exp) {
		super(t);
		Exp = exp;
	}
	
	@Override
	public Long evaluate() {
		return - Exp.evaluate();
	}

}
