package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import java.util.List;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public abstract class IExpression extends AnValue{
	
	protected IExpression left, right;
	
	public IExpression(Type t) {
		super(t);
		left = right = null;
	}

	public IExpression(Type t, IExpression v1) {
		super(t);
		left = v1;
		right = null;
	}
	
	public IExpression(Type t, IExpression v1, IExpression v2) {
		super(t);
		left = v1;
		right = v2;
	}

	public abstract Long evaluate();
	
	public Long evaluate(List<? extends AnValue> values) {
		setParameters(values);
		Long res = evaluate();
		setParameters(null);
		return res;
	}
	
	@Override
	public abstract IExpression clone();
	
	public void setParameters(List<? extends AnValue> values) {
		if (left != null)
			left.setParameters(values);
		if (right != null)
			right.setParameters(values);
	}

	public abstract boolean equalExpression(IExpression iExpression);

}
