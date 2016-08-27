package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class ConstExpression extends IExpression {
	
protected Long constExp;
	
	public ConstExpression(Type t, Long Const) {
		super(t);
		constExp = Const;
	}
	
	@Override
	public Long evaluate() {
		return constExp;
		
	}

	@Override
	public IExpression cloneExpression() {
		return new ConstExpression(type, constExp);
	}

	@Override
	public boolean canEvaluate() {
		return true;
	}
	
	public String toString() {
		return constExp.toString();
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof ConstExpression)) return false;
		return constExp.intValue() == ((ConstExpression)iExpression).constExp.intValue();
	}

}
