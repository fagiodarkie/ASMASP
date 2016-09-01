package com.laneve.asp.ASMAnalysis.tests;

public class MixedClass {

	public Thread t;
	public int i;
	
	MixedClass() {
		t = new Thread();
		i = 5;
	}

	MixedClass(int x) {
		this();
		i = x;
	}
	
	MixedClass(int x, Thread d) {
		this(x);
		t = d;
	}

	MixedClass(MixedClass o) {
		i = o.i;
		t = o.t;
	}
}
