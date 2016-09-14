package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class RemExpression extends IExpression {
	
	public RemExpression(Type t, IExpression left, IExpression right) {
		super(t, left, right);
	}
	
	@Override
	public Long evaluate() {
		return left.evaluate() % right.evaluate();
	}

	@Override
	public IExpression cloneExpression() {
		return new RemExpression(type, left.clone(), right.clone());
	}

	public String toString() {
		if (left.toString().contains(":unknown") || right.toString().contains(":unknown"))
			return "unknown";
		return "(" + left.toString() + " % " + right.toString() + ")";
	}
	
	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof RemExpression)) return false;
		return right.equalValue(((IExpression)iExpression).right) && left.equalValue(((IExpression)iExpression).left);
	}

}