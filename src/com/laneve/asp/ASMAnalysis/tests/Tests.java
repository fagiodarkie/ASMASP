package com.laneve.asp.ASMAnalysis.tests;

public class Tests {
	
	public static void main() throws InterruptedException {
		
		Release.test();
		Aliasing.test();
		DoubleRelease.test();
		Fields.test();
		ThreadFields.test();
		AnnidatedFields.test();
		Expressions.test();
		If.test();
		
		
	}
}
