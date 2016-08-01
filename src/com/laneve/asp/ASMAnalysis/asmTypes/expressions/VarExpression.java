package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import java.util.List;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class VarExpression extends IExpression {
	
	protected IExpression intExp;
	protected int index;
	
	public VarExpression(Type t, int position) {
		super(t);
		intExp = null;
		index = position;
	}
	
	@Override
	public void setParameters(List<? extends AnValue> parameters) {
		if (parameters == null || parameters.size() <= index) {
			intExp = null;
		}
		try {
			intExp = ((IExpression) parameters.get(index));
		} catch (Exception e) {
			// error
		}
	}
	
	@Override
	public Long evaluate() {
		if (intExp != null)
			return intExp.evaluate();
		throw new Error("Unable to evaluate expression if not fully defined.");
		
	}

	@Override
	public IExpression clone() {
		if (intExp == null)
			return new VarExpression(type, index);
		
		VarExpression v = new VarExpression(type, index);
		v.intExp = intExp.clone();
		return v;
		
	}

	public String toString() {
		return "x" + index;
	}

	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof VarExpression)) return false;
		return intExp == ((VarExpression)iExpression).intExp;
	}

}
