package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

public class ConstExpression implements IExpression {
	
protected Long constExp;
	
	public ConstExpression(Long Const) {
		constExp = Const;
	}
	
	@Override
	public Long evaluate() {
		return constExp;
		
	}

}
