package com.laneve.asp.ASMAnalysis.bTypes;

public interface IBheaviour {
	
	/**
	 * B takes a frame index, method name, stack and memory types as parameters,
	 * giving in return the behaviour at that point. Generating grammar:
	 * 
	 * B ::= atom ; b | [cond]b + [cond]b | atom
	 * atom ::= new Res | Res released | 0
	 * b ::= methodName_index(modified memory; modified stack)  
	 * 
	 * @return the string describing the behaviour at this instruction.
	 */

	public String printBehaviour();

}
