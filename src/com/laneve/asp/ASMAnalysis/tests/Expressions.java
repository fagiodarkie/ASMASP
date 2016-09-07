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
	
	public static int factWrong(int n) {
		return n * factWrong(n - 1);
	}
	
	public static int simpleOperations(int n, int m) {
		return 1 + (m - 2) * (n / 2);
	}
		
	public static void test() {
		
		int m = 10, n = 12;
		simpleOperations(n, m);
		if (m == 10) n = fact(m);
		else n = factWrong(n);
		simpleOperations(m, n);
		
		
		
	}

}
