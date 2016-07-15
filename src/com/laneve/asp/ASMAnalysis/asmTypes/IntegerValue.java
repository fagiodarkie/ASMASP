package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.expressions.IExpression;

public class IntegerValue extends AnValue {

	protected IExpression value;
	
	public IntegerValue(AnValue a) {
		super(a);
	}

	public void setValue(IExpression e) {
		value = e;
	}
	
	public Long getValue() {
		return value.evaluate();
	}

}
