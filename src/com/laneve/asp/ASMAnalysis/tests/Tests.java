package com.laneve.asp.ASMAnalysis.tests;

public class Tests {

	public int fieldOne, fieldTwo;
	public Thread t1;
	
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
		this(1);
	}
	
	public Tests(int i) {
		fieldOne = i;
		fieldTwo = i + 1;
		t1 = new Thread();
	}

	public Tests(Tests x) {
		fieldOne = x.fieldOne;
		fieldTwo = x.fieldTwo;
		t1 = x.t1;
	}
	
	public Tests(Tests x, Tests y) {
		fieldOne = x.fieldOne + y.fieldOne;
		fieldTwo = x.fieldTwo + y.fieldTwo;
		t1 = new Thread();
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
		
		return 3;
	}
	
	public static void main() throws InterruptedException {
		
		Tests w = new Tests(), h = new Tests(w);

		int a = 2;
		
		Tests x = new Tests(), y = new Tests(a),
			z = new Tests(x, y);
		x.fact(5);
		
		swap(x, y);
		swap(y, y);

		
		if (x.fieldOne - z.fieldOne == 0)
			a += x.bar();
	
		/*int b = a * 2 + x.foo(x.bar(a));
		
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
