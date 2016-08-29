package com.laneve.asp.ASMAnalysis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;

public class Names {

	private static class ParamAsString {
		public List<ParamAsString> params;
		public String name;
		
		public ParamAsString(AnValue val, Map<Long, String> idNames) {
			this.name = idNames.get(val.getID());
			params = new ArrayList<ParamAsString>();
			for (AnValue a : val.getFields()) 
				params.add(new ParamAsString(a, idNames));
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
		
		if (params.size() == 0)
			return "";
		
		String res = "";
		
		Map<Long, String> names = new HashMap<Long, String>();
		for (AnValue a : params) {
			fillMap(names, a, get(params.indexOf(a)));
			ParamAsString x = new ParamAsString(a, names);
			res += x.toString() + ",";
		}
		
		
		return res.substring(0, res.length() - (res.endsWith(",") ? 1 : 0));
	}

	public static String get(int i) {
		return alpha.substring(i, i + 1);
	}
	
	private static void fillMap(Map<Long, String> names, AnValue a, String name) {
		if (names.containsKey(a.getID()))
			return;
		names.put(a.getID(), name + (a instanceof ThreadValue ? ":" + ((ThreadValue)a).getStatus() : "" ));
		
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
}
