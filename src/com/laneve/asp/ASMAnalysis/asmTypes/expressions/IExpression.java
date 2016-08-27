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
	
	@Override
 	public abstract String toString();
	
	public IExpression evaluate(List<? extends AnValue> values) {
		setParameters(values);
		IExpression res;
		try {
			res = new ConstExpression(Type.INT_TYPE, evaluate());
		} catch(Error e) {
			res = this;
		}
		setParameters(null);
		return res;
	}
	
	@Override
	public IExpression clone() {
		IExpression a = cloneExpression();
		// a.ID = ID;
		a.name = name;
		a.fieldName = fieldName;
		a.isVariable = isVariable;
		a.updated = updated;
		return a;
	}
	
	
	public abstract IExpression cloneExpression();
	
	public void setParameters(List<? extends AnValue> values) {
		if (left != null)
			left.setParameters(values);
		if (right != null)
			right.setParameters(values);
	}

	public abstract boolean equalValue(AnValue other);

	public void setType(Type t) {
		type = t;
	}

	public boolean canEvaluate() {
		boolean res = true;
		if (left != null)
			res = res && left.canEvaluate();
		if (right != null)
			res = res && right.canEvaluate();
		return res;
	}

}
