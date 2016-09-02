package com.laneve.asp.ASMAnalysis.tests;

public class Release {
	
	public static void release(Thread x) throws InterruptedException {
		x.join();
	}

	public static void test() throws InterruptedException {
		
		Thread a = new Thread(), b = new Thread();
		
		a.run(); b.run();
		
		release(a);
		release(a);
		release(b);
		a = new Thread();
		a.run();
		release(a);
		release(b);
		
	}

}
