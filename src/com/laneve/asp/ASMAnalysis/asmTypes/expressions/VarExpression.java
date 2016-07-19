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
	public void setParameters(List<AnValue> parameters) {
		if (parameters == null || parameters.size() <= index) {
			intExp = null;
			return;
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

}
