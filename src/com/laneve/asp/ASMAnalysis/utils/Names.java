package com.laneve.asp.ASMAnalysis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.UnknownExpression;

public class Names {

	private static class ParamAsString {
		protected List<ParamAsString> params;
		protected String name;
		protected ThreadValue thread;
		protected IExpression exp;
		
		public ParamAsString(AnValue val, Map<Long, String> idNames) {
			
			this.name = idNames.get(val.getID());
			params = new ArrayList<ParamAsString>();
			
			if (val instanceof IExpression) {
				exp = (IExpression) val.clone();
				thread = null;
				if (val instanceof UnknownExpression)
					this.name += ":unknown";
			} else if (val instanceof ThreadValue) {
				exp = null;
				thread = (ThreadValue) val.clone();
			} else for (AnValue a : val.getFields()) {
				params.add(new ParamAsString(a, idNames));
			} 
		}
		
		public String toString() {
			String p = (params.size() > 0 ? "[" : "");
			if (p.length() > 0) {
				for (ParamAsString par: params)
					p += par.toString() + ",";
				p = p.substring(0, p.length() - 1) + "]";
			}
			
			return name + p;
		}

		public String toStringWithValues() {
			String p = (params.size() > 0 ? "[" : "");
			if (p.length() > 0) {
				for (ParamAsString par: params)
					p += par.toString() + ",";
				p = p.substring(0, p.length() - 1) + "]";
			} else {
				if (exp != null)
					return (exp instanceof UnknownExpression ? name : name + ":" + exp.toString());
				else if (thread != null)
					return name ;
			}
			
			return name + p;
		}
	}
	
	public static final String alpha = "abcdefghijklmnopqrstuvwxyz";

	public static List<String> getSingleParameters(String parameters) {
		
		List<String> l = new ArrayList<String>();
		
		int beginIndex = 0;
		int openSquares = 0;
		for (int i = 0; i < parameters.length(); ++i) {
			
			if (openSquares == 0 && parameters.charAt(i) == ',') {
				l.add(parameters.substring(beginIndex, i));
				beginIndex = i + 1;
			} else if (parameters.charAt(i) == '[')
				openSquares ++;
			else if (parameters.charAt(i) == ']')
				openSquares --;			
		}
		
		if (beginIndex < parameters.length())
			l.add(parameters.substring(beginIndex));
		
		return l;
	}

	public static String computeParameterList(List<AnValue> params) {
		
		String res = "";
		
		for (ParamAsString x : computeParams(params))
			res += x.toString() + ",";
	
		String r = res.substring(0, res.length() - (res.endsWith(",") ? 1 : 0));
//		System.out.println(r);
		return r;
	}

	private static List<ParamAsString> computeParams(List<AnValue> params) {
		
		List<ParamAsString> res = new ArrayList<ParamAsString>();
		if (params.size() == 0)
			return res;
		
		Map<Long, String> names = new HashMap<Long, String>();
		for (AnValue a : params)
			fillMap(names, a, get(params.indexOf(a)));
		for (AnValue a : params) {
			ParamAsString x = new ParamAsString(a, names);
			res.add(x);
		}
		return res;
	}

	public static String get(int i) {
		return alpha.substring(i, i + 1);
	}
	
	private static void fillMap(Map<Long, String> names, AnValue a, String name) {
		if (names.containsKey(a.getID()))
			return;
		String extra = "";
		if (a instanceof ThreadValue) {
			int x = ((ThreadValue)a).getStatus();
			extra = ":" + x;
		}
			
		names.put(a.getID(), name + extra);
		
		for (String fieldName: a.getFieldNames())
			fillMap(names, a.getField(fieldName), name + "." + fieldName);
	}

	public static String normalizeClassName(String className) {
		if (className.endsWith(";"))
			className = className.substring(0, className.length() - 1);
		if(className.startsWith("L") || className.startsWith("I"))
			className = className.substring(1);
		return className.replace('.', '/');
	}


	public static int getPos(String substring) {
		return alpha.indexOf(substring.substring(0, 1));
	}


	public static String computeParameterListWithValues(List<AnValue> vals) {
		String res = "";
		
		for (ParamAsString x : computeParams(vals))
			res += x.toStringWithValues() + ",";
	
		String r = res.substring(0, res.length() - (res.endsWith(",") ? 1 : 0));
//		System.out.println(r);
		return r;
	}
}
