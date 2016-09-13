package com.laneve.asp.ASMAnalysis.asmTypes.expressions;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class FunctionCallExpression extends IExpression {

	private String methodName;
	private List<? extends AnValue> values;

	public FunctionCallExpression(Type t, String m) {
		super(t);
		methodName = m;
	}
	
	@Override
	public void setParameters(List<? extends AnValue> vals) {
		values = vals;
	}
		
	@Override
	public boolean canEvaluate() {
		return false;
	}
	
	@Override
 	public Long evaluate() {
		return null;
	}

	@Override
	public String toString() {
		String ret = methodName.split("\\(")[0];
		ret = ret.substring(ret.lastIndexOf("/") + 1) + "(";
		for (AnValue a: values) {
			if (a instanceof IExpression) {
				IExpression x = ((IExpression)a);
				if (x.canEvaluate())
					ret += x.evaluate() + ", ";
				else
					ret += a.toString() + ", ";
			} else ret += a.toString() + ", ";
		}
		if (ret.endsWith(", "))
			ret = ret.substring(0, ret.lastIndexOf(", "));
		
		if (ret.substring(ret.indexOf('(')).contains("unknown"))
			return "unknown";
		return ret + ")";
	}

	@Override
	public IExpression cloneExpression() {
		List<AnValue> l = new ArrayList<AnValue>();
		for (AnValue a: values)
			l.add(a.clone());
		
		FunctionCallExpression r = new FunctionCallExpression(type, methodName);
		
		r.setParameters(l);
		return r;
	}

	@Override
	public boolean equalValue(AnValue other) {
		if (!(other instanceof FunctionCallExpression)) return false;
		FunctionCallExpression o = (FunctionCallExpression) other;
		if (!o.methodName.equalsIgnoreCase(methodName))
			return false;
		for (int i = 0; i < values.size(); ++i)
			if (!values.get(i).equalValue(o.values.get(i)))
				return false;
		return true;
	}

}
