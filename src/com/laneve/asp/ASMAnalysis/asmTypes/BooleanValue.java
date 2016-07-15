package com.laneve.asp.ASMAnalysis.asmTypes;

import com.laneve.asp.ASMAnalysis.asmClasses.expressions.IBoolExpression;

public class BooleanValue extends AnValue {

	protected IBoolExpression value;
	
	public BooleanValue(AnValue a) {
		super(a);
	}
	
	public void setValue(IBoolExpression e) {
		value = e;
	}
	
	public boolean getValue() {
		return value.evaluate();
	}

}
