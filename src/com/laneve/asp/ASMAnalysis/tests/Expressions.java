package com.laneve.asp.ASMAnalysis.tests;

public class Expressions {

	public static int fact(int n){
		 
		int m;

		if (n == 0) return 1;
		else { 
			m = fact(n-1);
			return m * n;
		}
	}
		
	public static void test() {
		fact(100); //not typable because of missing IF semantics.
		
		
		
	}

}
