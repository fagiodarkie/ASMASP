package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

/**
 * This class maps the simple transitions of the type:
 * 
 * B(i, memory, stack) = atom; B(j, memory', stack').
 * 
 * Created when new resources are allocated or deallocated.
 */

public class ChainedBehavior extends SimpleBehaviour {

	protected Atom atom;
	
	public ChainedBehavior(int atomType,
			String source, int line1, List<AnValue> sourceList,
			String target, int line2, List<AnValue> targetList) {
		super(source, line1, sourceList, target, line2, targetList);
		atom = new Atom(atomType);
	}
	
	
	public String printBehaviour() {
		return getCaption() + " = " + atom.printBehaviour() + "; " + getBody();
	}

}
