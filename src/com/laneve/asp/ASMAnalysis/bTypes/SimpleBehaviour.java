package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

/**
 * This class maps the simple transitions of the type:
 * 
 * B(i, memory, stack) = B(j, memory', stack').
 * 
 * No atoms or branches are mapped. GOTO falls in this category.
 * 
 * To map various instructions that may have the same index but belonging to
 * different classes / methods, the generic name 'B' is substituted by the name of
 * the current class and line number of the classfile. A map should be kept to keep
 * track of the correspondence between classfile line numbers and labels.
 * 
 * Example:
 * 
 * java.lang.String#14(x1:int, x2:int, x3:boolean) = java.lang.String#15(x1, x2 + 1)
 * 
 * java.lang.String#24(x1:int) = java.lang.String#21(x1, 1)
 *
 * @author jacopo.freddi
 *
 */
public class SimpleBehaviour implements IBehaviour {

	protected String sourceClass, targetClass;
	protected int sourceLine, targetLine;
	protected List<AnValue> sourceParameters, targetParameters;
	
	public SimpleBehaviour(String source, int line1, List<AnValue> sourceList,
			String target, int line2, List<AnValue> targetList) {
		sourceClass = source;
		targetClass = target;
		sourceLine = line1;
		targetLine = line2;
		sourceParameters = sourceList;
		targetParameters = targetList;
	}
	
	@Override
	public String printBehaviour() {
		return getCaption() + " = " + getBody();
	}
	
	protected String getCaption() {
		String res = sourceClass + "#" + sourceLine + "(";
		for (AnValue a: sourceParameters) {
			if(sourceParameters.indexOf(a) > 0)
				res += ", ";
			res += a.toString();
		}
		res += ")";
		return res;
	}
	
	protected String getBody() {
		String res = targetClass + "#" + targetLine + "("; 
		for (AnValue a: targetParameters) {
			if(targetParameters.indexOf(a) > 0)
				res += ", ";
			res += a.getValue();
		}
		res += ")";
		return res;
	}

}
