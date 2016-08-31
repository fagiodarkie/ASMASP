package com.laneve.asp.ASMAnalysis.tests;

public class DoubleRelease {

	
	public static void doubleRelease(Thread x, Thread y) throws InterruptedException {
		x.join();
		y.join();
	}

	public static void user1() throws InterruptedException {
		Thread x = new Thread(), y = new Thread();
		x.run();
		y.run();
		doubleRelease(x, y);
		x.run();
		doubleRelease(x, x);
		doubleRelease(x, y);
		doubleRelease(x, x);

	}
	public static void test() throws InterruptedException {
		user1();
	}

}
