package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class MinusExpression extends IExpression {
	
	public MinusExpression(Type t, IExpression exp) {
		super(t, exp);
	}
	
	@Override
	public Long evaluate() {
		return - left.evaluate();
	}

	@Override
	public IExpression cloneExpression() {
		return new MinusExpression(type, left.clone());
	}

	public String toString() {
		if (left.toString().contains("unknown"))
			return "unknown";
		return "(-" + left.toString() + ")";
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof MinusExpression)) return false;
		return left.equalValue(((IExpression)iExpression).left);
	}

}
