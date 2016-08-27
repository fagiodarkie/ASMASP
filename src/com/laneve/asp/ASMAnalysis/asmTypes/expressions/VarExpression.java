package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import java.util.List;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.utils.Names;

public class VarExpression extends IExpression {
	
	protected IExpression intExp;
	protected int index;
	
	public VarExpression(Type t, int position) {
		super(t);
		intExp = null;
		index = position;
	}
	
	public VarExpression(Type t, int position, String f) {
		this(t, position);
		name = f;
	}
	
	@Override
	public void setParameters(List<? extends AnValue> parameters) {
		if (parameters == null || parameters.size() <= index) {
			intExp = null;
		}
		try {
			String m = name;
			if (m.split("\\.").length == 1)
				intExp = ((IExpression) parameters.get(index));
			else {
				String f = m.substring(m.indexOf('.') + 1);
				int paramPosition = Names.alpha.indexOf(m.substring(0, m.indexOf('.')));
				intExp = ((IExpression) parameters.get(paramPosition).getField(f));
			}
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
	public IExpression cloneExpression() {
		if (intExp == null)
			return new VarExpression(type, index, name);
		
		VarExpression v = new VarExpression(type, index, name);
		v.intExp = intExp.clone();
		return v;
		
	}

	public String toString() {
		return name;
	}

	@Override
	public boolean canEvaluate() {
		return intExp != null && intExp instanceof ConstExpression;
	}
	
	@Override
	public boolean equalValue(AnValue iExpression) {
		if (!(iExpression instanceof VarExpression)) return false;
		VarExpression i = ((VarExpression)iExpression);
		return intExp == i.intExp || name.equalsIgnoreCase(i.name);
	}

}
