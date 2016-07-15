package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class SumExpression extends IExpression {

	protected IExpression leftExp, rightExp;
	
	public SumExpression(Type t, IExpression left, IExpression right) {
		super(t);
		leftExp = left;
		rightExp = right;
	}
	
	@Override
	public Long evaluate() {
		return leftExp.evaluate() + rightExp.evaluate();
	}

}
