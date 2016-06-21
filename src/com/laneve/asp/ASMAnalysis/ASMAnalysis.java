package com.laneve.asp.ASMAnalysis;

public class ASMAnalysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* TODO
		 * - find and load class files;
		 * - input the class files for analysis
		 * ?- sort them in order to have other methods analyzed before they are needed?
		 * - invoke analyzer methods;
		 * - get frames;
		 * |-- ! it may be the case that each frame only gives one "Value". If so, cycles would not be "repeated" n times
		 * 		unless we generate the behavioural type while analysis goes on. Maybe it's better to have a 
		 * 		parameterized Value that reflect the types we defined (B_i(Frame, Stack) ::= type | type;B_j(F',S') | B_j(F',S') | [cond]B + [cond]B 
		 * - print frames result. 
		 */

	}

}
