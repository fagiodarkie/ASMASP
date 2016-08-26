package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class SubExpression extends IExpression {
	
	public SubExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() - right.evaluate();
	}

	@Override
	public IExpression clone() {
		return new SubExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		return "(" + left.toString() + " - " + right.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof SubExpression)) return false;
		if (!right.equalValue(((IExpression)iExpression).right)) return false;
		if (!left.equalValue(((IExpression)iExpression).left)) return false;
		return true;
	}

}
