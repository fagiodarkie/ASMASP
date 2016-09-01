package com.laneve.asp.ASMAnalysis.tests;

public class OuterClass {

	public MixedClass m1, m2;
	public int time;
	
	OuterClass() {
		m1 = new MixedClass();
		m2 = new MixedClass();
		time = 0;
	}
	
	OuterClass(MixedClass m) {
		m1 = m;
		m2 = m;
		time = 1;
	}

	OuterClass(MixedClass m, MixedClass n) {
		this(m);
		m2 = n;
		time ++;
	}

	OuterClass(OuterClass o) {
		this(o.m1, o.m2);
		time ++;
	}
}
