package com.laneve.asp.ASMAnalysis.asmClasses;

import org.objectweb.asm.tree.analysis.Frame;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;


public class ValFrame extends Frame<AnValue> {

	public ValFrame(Frame<AnValue> src) {
		super(src);
		// TODO Auto-generated constructor stub
	}

	
	/*
	 * 
	 * eg: in IADD
	 * 
	 * 1) compute list of rebased AnValues (values become "x_i")
		 * value2 = "x" + getstacksize
		 * value1 = "x" + getstacksize - 1
	 * 2) save it
	 * 3) super.visit
	 * 4) compute list of new AnValues (keeping values of the kind "x_i + x_j")
	 * 5) save it
	 * 6) backup the relevant Behavioural type from the lists.
	 * 
	 * TODO: understand how line index works.
	 * 
	 * in interpreter:
	 * return value1.value "+" value2.value
	 * 
	 */

}
