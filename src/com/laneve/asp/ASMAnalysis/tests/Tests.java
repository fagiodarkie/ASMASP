package com.laneve.asp.ASMAnalysis.tests;

public class Tests {
	
	public int fieldOne, fieldTwo;
	public Thread t1;
	public Test2 t;
	
	public int foo(int a) {
		return a * bar(a);
	}
	
	public void release(Thread t, Thread t2, int i) throws InterruptedException {
		t.join();
		
		t = new Thread();
		t.run();
		t.join();
		
		t2.join();
	}
	
/*	public Thread create() {
		Thread t = new Thread();
		t.run();
		return t;
	}
*/	
	public int fact(int n) {
		return n * fact(n - 1);
	}
	
	public int bar(int a) {
		
		Thread t = new Thread();
		t.run();
		int x = a * (a + 3);
		
		return x;
	}

	public void rewrite (Tests b) {
		b.fieldTwo = fieldOne;
	}
	
	public Tests() {
		this(1);
	}
	
	public Tests(int i) {
		fieldOne = i;
		fieldTwo = i + 1;
		t = new Test2();
		t1 = new Thread();
	}

	public Tests(Tests x) {
		fieldOne = x.fieldOne;
		fieldTwo = x.fieldTwo;
		t1 = x.t1;
		t = x.t;
	}
	
	public Tests(Tests x, Tests y) {
		fieldOne = x.fieldOne + y.fieldOne;
		fieldTwo = x.fieldTwo + y.fieldTwo;
		x.t1 = y.t1;
		t = x.t;
		t1 = x.t1;
		t1.run();
	}
	
	public static void swap(Tests a, Tests b) {
		int temp = a.fieldOne;
		a.fieldOne = b.fieldOne;
		b.fieldOne = temp;
	}
	
	public int bar() throws InterruptedException {
		
		fieldOne = 3;
		Thread t5 = new Thread();
		
		t5.run();
		t5.join();
		t1.join();
		
		return fieldOne;
	}
	
	public static void main() throws InterruptedException {
		
//		Tests w = new Tests(), h = new Tests(w);

		int a = 2;
		
		Tests x = new Tests();
		Tests y = new Tests(x, x),
			z = new Tests(x, y);
		x.fact(5);
		y.rewrite(z);
//		swap(x, y);
//		swap(y, y);

		
		//if (x.fieldOne - z.fieldOne == 0)
		//	a += x.bar();
	
		int b = a * 2 + x.foo(x.bar(a));
		
		Thread t = new Thread();
		Thread t2 = new Thread();
		t.run();
		t2.run();
		x.release(t, t2, 0);
		
		x.release(t, t2, 3);
		t = new Thread();		
		t.run();
//		x.release(t, t, b);
		
		
	}
}
