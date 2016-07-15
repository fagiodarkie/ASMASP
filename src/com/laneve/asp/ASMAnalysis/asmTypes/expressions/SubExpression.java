package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class SubExpression extends IExpression {
	
protected IExpression leftExp, rightExp;
	
	public SubExpression(Type t, IExpression left, IExpression right) {
		super(t);
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public Long evaluate() {
		return leftExp.evaluate() - rightExp.evaluate();
	}

}
