package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.utils.Names;


public class MethodBehaviour implements IBehaviour {

	private String methodName;
	private List<? extends AnValue> vals;
	private String pattern;

	public MethodBehaviour(String currentMethodName,
			List<? extends AnValue> values) {
		methodName = currentMethodName;
		vals = values;
		pattern = "";
	}

	public MethodBehaviour(String currentMethodName, String methodParametersPattern, List<? extends AnValue> c) {
		methodName = currentMethodName;
		pattern = methodParametersPattern;
		vals = c;
		
	}

	@Override
	public boolean equalBehaviour(IBehaviour updatedBehaviour) {
		boolean r = (updatedBehaviour instanceof MethodBehaviour) && ((MethodBehaviour)updatedBehaviour).methodName.equalsIgnoreCase(methodName);
		if (!r)
			return false;
		MethodBehaviour o = (MethodBehaviour) updatedBehaviour;
		if (vals == null && o.vals == null
				&& pattern == o.pattern)
			return true;
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

	@SuppressWarnings("unchecked")
	public String toString() {
		String ret = methodName.split("\\(")[0];
		ret = ret.substring(ret.lastIndexOf("/") + 1) + "(";
		
		if (vals == null)
			return ret + pattern + ")";
		else
			return ret + Names.computeParameterList((List<AnValue>)vals) + ")";
		
		
//		List<ThreadValue> t = new ArrayList<ThreadValue>();
//		for (AnValue a: vals) {
//			if (a instanceof IExpression) {
//				IExpression x = ((IExpression)a);
//				if (x.canEvaluate()) {
//					ret += x.evaluate() + ", ";
//				} else {
//					ret += x.toString();
//				}
//			}  else if (a instanceof ThreadValue) {
//				ThreadValue x = (ThreadValue)a;
//				boolean found = false;
//				for (int i = 0; i < t.size(); ++i) {
//					if (t.get(i).equalThread(x)) {
//						found = true;
//						break;
//					}
//				}
//				if (!found) {
//					ret += a.toString() + ", ";
//					t.add(x);
//				}
//			} else ret += a.toString() + ", ";
//		}
//		if (ret.endsWith(", "))
//			ret = ret.substring(0, ret.lastIndexOf(", "));
//		
//		return ret + ")";
	}
	
}
