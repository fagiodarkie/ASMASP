package com.laneve.asp.ASMAnalysis.tests;

public class Tests {
	
	public static void main() throws InterruptedException {
		
		Aliasing.test();
		Release.test();
		DoubleRelease.test();
		Fields.test();
		ThreadFields.test();
		AnnidatedFields.test();
		Expressions.test();
		If.test();
		
		
	}
}
