package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class DivExpression extends IExpression {
	
	public DivExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		if (right.evaluate() != 0){
			return left.evaluate() / right.evaluate();
		}
		else {
			throw new Error("Division by zero detected.");
		}
	}

	@Override
	public IExpression cloneExpression() {
		return new DivExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " / " + right.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof DivExpression)) return false;
		return right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left);
	}

}