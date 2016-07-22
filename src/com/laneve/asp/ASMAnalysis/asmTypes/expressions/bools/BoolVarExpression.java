package com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools;
import java.util.List;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;

public class BoolVarExpression extends IBoolExpression {
	
	protected IBoolExpression exp;
	protected int index;
	
	public BoolVarExpression(Type t, int position) {
		super(t);
		exp = null;
		index = position;
	}
	
	@Override
	public void setParameters(List<AnValue> parameters) {
		if (parameters == null || parameters.size() <= index) {
			exp = null;
			return;
		}
		try {
			if (parameters.get(index) instanceof IExpression) {
				Long r = ((IExpression) parameters.get(index)).evaluate();
				if (r == 0)
					exp = new FalseExpression(type);
				else exp = new TrueExpression(type);
			} else exp = ((IBoolExpression) parameters.get(index));
		} catch (Exception e) {
			// error
		}
	}
	
	@Override
	public boolean evaluate() {
		if (exp != null)
			return exp.evaluate();
		throw new Error("Unable to evaluate expression if not fully defined.");
		
	}

}
