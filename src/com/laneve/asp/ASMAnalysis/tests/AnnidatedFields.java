package com.laneve.asp.ASMAnalysis.tests;

public class AnnidatedFields {

	public static void f(MixedClass m, OuterClass o) {
		MixedClass x = o.m1;
		o.m1 = m;
		m.i = x.i;
		m.t = x.t;
	}
	
	public static void test() {
		MixedClass ma = new MixedClass(), mb = new MixedClass(4);
		
		OuterClass o1 = new OuterClass(), o2 = new OuterClass(ma), o3 = new OuterClass(ma, mb),
				o4 = new OuterClass(o3);
		
		f(ma, o3);
		f(o2.m1, o2);
		
		f(o1.m2, o4);
	}

}
