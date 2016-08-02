package com.laneve.asp.ASMAnalysis.tests;

public class Tests {

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
	
	public int bar(int a) {
		
		Thread t = new Thread();
		t.run();
		int x = a * (a + 3);
		
		return x;
	}
	
	public int bar() throws InterruptedException {
		
		Thread t5 = new Thread();
		
		t5.run();
		t5.join();
		
		return 2;
	}
	
	public static void main() throws InterruptedException {
		
		Tests x = new Tests();
	
		/*int a = x.foo(3);
		
		a += x.bar();
		
		int b = a * 2 + x.foo(x.bar(a));
		
		Thread t1 = new Thread(),
			t2 = new Thread(),
			t3 = new Thread(),
			t4 = new Thread();
		
		t1.run();
		t2.run();
		t3.run();
		t1.join();
		t4.run();
		t3.join();
		t4.join();*/
		Thread t = new Thread();
		t.run();
		x.release(t, new Thread(), 0);
		
		
	}
}
