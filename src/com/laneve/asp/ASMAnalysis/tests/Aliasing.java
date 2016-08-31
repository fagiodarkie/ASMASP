package com.laneve.asp.ASMAnalysis.tests;

public class Aliasing {

	public static void f(int a, int b) {
		a += b;
	}
	
	public static void f(MixedClass a, MixedClass b) {
		a.i += b.i;
	}
	
	public static void swapThreads(MixedClass a, MixedClass b) {
		Thread tmp = a.t;
		a.t = b.t;
		b.t = tmp;
	}
	
	public static void duplicateThreads(MixedClass a, MixedClass b) {
		a.t = b.t;
	}
	
	
	public static void test() {
		int a = 1, b = 2, c = 3;
		
		f(a, b);
		f(a, c);
		f(a, a);
		c = a;
		f(a, c);
		
		MixedClass x = new MixedClass(), y = new MixedClass();
		
		f(x, y);
		f(x, x);
		MixedClass z = y;
		f(x, z);
		swapThreads(x, y);
		f(x, y);
		duplicateThreads(x, y);
		f(x, y);
	}

}
