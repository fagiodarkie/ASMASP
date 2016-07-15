package com.laneve.asp.ASMAnalysis.asmTypes;

public class IntegerValue extends AnValue {

	protected IExpression value;
	
	public IntegerValue(AnValue a) {
		super(a);
		// TODO Auto-generated constructor stub
	}
	
	public Long getValue() {
		return value.evaluate();
	}

}
