package com.laneve.asp.ASMAnalysis.tests;

public class Expressions {

	public static int fact(int n){
		 
		int x, m;

		if (n == 0) return 1;
		else { 
			x = fact(n-1); 
			m = x; 
			return m*n;
		}
	}
		
	public static void test() {
		// fact(100); not typable because of missing IF semantics.
		
		
		
	}

}
