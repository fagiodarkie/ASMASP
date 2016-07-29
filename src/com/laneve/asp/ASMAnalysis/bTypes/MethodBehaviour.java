package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;


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
		return (updatedBehaviour instanceof MethodBehaviour) && ((MethodBehaviour)updatedBehaviour).methodName.equalsIgnoreCase(methodName);
	}

	public IBehaviour clone() {
		return new MethodBehaviour(methodName, vals);
	}
	
	@Override
	public boolean equal(IBehaviour o) {
		if (!equalBehaviour(o)) return false;
		
		MethodBehaviour other = (MethodBehaviour)o;
		if (other.vals.size() != vals.size()) return false;
		// TODO
		return true;
	}

	@Override
	public void mergeWith(IBehaviour frameBehaviour) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		String ret = methodName.substring(methodName.lastIndexOf("/") + 1, methodName.indexOf("(")  + 1);
		for (AnValue a: vals)
			ret += a.toString() + ", ";
		
		if (ret.endsWith(", "))
			ret = ret.substring(0, ret.lastIndexOf(", "));
		
		return ret + ")";
	}
	
}
