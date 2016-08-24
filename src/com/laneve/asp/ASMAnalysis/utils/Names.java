package com.laneve.asp.ASMAnalysis.utils;

import java.util.ArrayList;
import java.util.List;

public class Names {

	public static final String alpha = "abcdefghijklmnopqrstuvwxyz";

	public static List<String> getSingleParameters(String parameters) {
		
		List<String> l = new ArrayList<String>();
		
		int beginIndex = 0;
		int openSquares = 0;
		for (int i = 0; i < parameters.length(); ++i) {
			
			if (openSquares == 0 && parameters.charAt(i) == ',') {
				l.add(parameters.substring(beginIndex, i - 1));
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

}
