package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

/**
 * Class used, for instance, in typical "return" instruction. No further behavior may be
 * computed after this point.
 * 
 * B_i(x1, x2, x3) = 0
 * 
 */
public class FinalBehaviour extends SimpleBehaviour {
	
	public FinalBehaviour(String source, int line1, List<AnValue> sourceList) {
		super(source, line1, sourceList, "", 0, null);
		// probably the atom will always be a RETURN. all other instructions will
		// go further on, while a RETURN instruction is found.
	}
	
	public String printBehaviour() {
		return getCaption() + " = 0";
	}

}
