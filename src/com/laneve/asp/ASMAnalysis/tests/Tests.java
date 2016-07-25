package com.laneve.asp.ASMAnalysis.tests;

public class Tests {

	public int foo(int a) {
		return a * bar(a);
	}
	
	public int bar(int a) {
		return a * (a + 3);
	}
	
	public int bar() {
		return 2;
	}
	
	public static void main() {
		
		Tests x = new Tests();
	
		int a = x.foo(3);
		
		a += x.bar();
		
		int b = a * 2 + x.foo(x.bar(a));
		
	}
}
