package com.laneve.asp.ASMAnalysis.asmClasses;
import org.objectweb.asm.tree.analysis.Frame;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;


public class BehaviourFrame extends Frame<AnValue> {

	public BehaviourFrame(Frame<? extends AnValue> src) {
		super(src);
	}
	
	public BehaviourFrame(int a, int b) {
		super(a, b);
	}
	
	

}
