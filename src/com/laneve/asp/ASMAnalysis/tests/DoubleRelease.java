package com.laneve.asp.ASMAnalysis.tests;

public class DoubleRelease {

	
	public static void doubleRelease(Thread x, Thread y) throws InterruptedException {
		x.join();
		y.join();
	}

	public static void doubleRelease(OuterClass o) throws InterruptedException {
		doubleRelease(o.m1.t, o.m2.t);
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
		
		OuterClass o1 = new OuterClass(), o2 = new OuterClass();
		o1.m1.t.run();
		o1.m2.t.run();
		o2.m1.t.run();
		o2.m2.t.run();
		doubleRelease(o1);
		doubleRelease(o1);
		o2.m1.t = o2.m2.t;
		doubleRelease(o2);
	}

}
