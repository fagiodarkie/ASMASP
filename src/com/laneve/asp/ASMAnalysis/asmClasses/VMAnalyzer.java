package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class VMAnalyzer extends Analyzer<AnValue> {

	protected AnalysisContext context;

	public VMAnalyzer(Interpreter<AnValue> arg0, AnalysisContext context) {
		super(arg0);
		this.context = context;
	}
	
	@Override
	public BehaviourFrame[] analyze(final String owner, final MethodNode m)
            throws AnalyzerException {
		
		String methodName = m.name;
		
		List<String> deps = new ArrayList<String>();
		for (int i = 0; i < m.instructions.size(); ++i) {
			if (m.instructions.get(i) instanceof InvokeDynamicInsnNode) {
				deps.add(((InvokeDynamicInsnNode) m.instructions.get(i)).name);
				
			}
		}
		context.signalDependancy(methodName, deps);
		
		// TODO
		return null;
	}
}
