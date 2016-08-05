package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;

import java.util.List;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public abstract class IBoolExpression extends AnValue {
	
	protected IExpression left = null, right = null;
	protected IBoolExpression leftB = null, rightB = null;
	
	public IBoolExpression(Type t) {
		super(Type.BOOLEAN_TYPE);
	}
	
	public IBoolExpression(Type t, IExpression v1) {
		super(Type.BOOLEAN_TYPE);
		left = v1;
	}
	
	public IBoolExpression(Type t, IExpression v1, IExpression v2) {
		super(Type.BOOLEAN_TYPE);
		left = v1;
		right = v2;
	}

	public IBoolExpression(Type t, IBoolExpression v1) {
		super(Type.BOOLEAN_TYPE);
		leftB = v1;
	}
	
	public IBoolExpression(Type t, IBoolExpression v1, IBoolExpression v2) {
		super(Type.BOOLEAN_TYPE);
		leftB = v1;
		rightB = v2;
	}

	public abstract boolean evaluate();

	public boolean evaluate(List<AnValue> parameters) {
		setParameters(parameters);
		boolean res = evaluate();
		setParameters(null);
		return res;
	}
	
	public void setParameters(List<AnValue> parameters) {
		if (left != null)
			left.setParameters(parameters);
		if (right != null)
			right.setParameters(parameters);
		if (leftB != null)
			leftB.setParameters(parameters);
		if (rightB != null)
			rightB.setParameters(parameters);
	}

	public boolean equal(IBoolExpression other) {
		
		if (!(getClass().isInstance(other)))
			return false;
		
		boolean res = true;
		if (left != null)
			res &= left.equalValue(other.left);
		if (right != null)
			res &= right.equalValue(other.right);
		if (leftB != null)
			res &= leftB.equal(other.leftB);
		if (rightB != null)
			res &= rightB.equal(other.rightB);
			
		return res;
	}

	public abstract String toString();
	
}
