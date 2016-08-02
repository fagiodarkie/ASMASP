package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;


public class MethodBehaviour implements IBehaviour {

	private String methodName;
	private List<? extends AnValue> vals;

	public MethodBehaviour(String currentMethodName,
			List<? extends AnValue> values) {
		methodName = currentMethodName;
		vals = values;
	}

	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		boolean r = (updatedBehaviour instanceof MethodBehaviour) && ((MethodBehaviour)updatedBehaviour).methodName.equalsIgnoreCase(methodName);
		if (!r)
			return false;
		MethodBehaviour o = (MethodBehaviour) updatedBehaviour;
		if (o.vals.size() != vals.size())
			return false;
		for (int i = 0; i < vals.size(); ++i) {
			if (!(vals.get(i)).equalValue(o.vals.get(i)))
				return false;
		}
		return true;
	}

	public IBehaviour clone() {
		return new MethodBehaviour(methodName, vals);
	}
	
	@Override
	public boolean equal(IBehaviour o) {
		return equalBehaviour(o);
	}

	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		String ret = methodName.split("\\(")[0];
		ret = ret.substring(ret.lastIndexOf("/") + 1) + "(";
		for (AnValue a: vals) {
			if (a instanceof IExpression) {
				try {
					ret += ((IExpression)a).evaluate() + ", ";
				} catch (Throwable t) {
					ret += a.toString() + ", ";
				}
			} else ret += a.toString() + ", ";
		}
		if (ret.endsWith(", "))
			ret = ret.substring(0, ret.lastIndexOf(", "));
		
		return ret + ")";
	}
	
}
