package com.laneve.asp.ASMAnalysis.tests;

public class Tests {

	public int fieldOne;
	
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
	
	public Thread create() {
		Thread t = new Thread();
		t.run();
		return t;
	}
	
	public int fact(int n) {
		return n * fact(n - 1);
	}
	
	public int bar(int a) {
		
		Thread t = new Thread();
		t.run();
		int x = a * (a + 3);
		
		return x;
	}

	public Tests() {
		fieldOne = 3;
	}
	
	public Tests(int i) {
		fieldOne = i;
	}

	public Tests(Tests x) {
		fieldOne = x.fieldOne;
	}
	
	public int bar() throws InterruptedException {
		
		fieldOne = 3;
		Thread t5 = new Thread();
		
		t5.run();
		t5.join();
		
		return 3;
	}
	
	public static void main() throws InterruptedException {
		
		Tests x = new Tests(), y = new Tests(2), z = new Tests(y);
	
		x.fact(5);
		
		int a = 3;
		
		if (x.fieldOne - z.fieldOne == 0)
			a += x.bar();
		
/*		int b = a * 2 + x.foo(x.bar(a));
		
		Thread t = new Thread();
		Thread t2 = new Thread();
		t.run();
		t2.run();
		x.release(t, t2, 0);
		
		t = new Thread();		
		t.run();
		x.release(t, t, b);*/
		
		
	}
}
