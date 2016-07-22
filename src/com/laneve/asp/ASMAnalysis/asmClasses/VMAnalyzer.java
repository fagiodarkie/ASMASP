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
		
		String methodName = owner + "." + m.name;
		BehaviourFrame[] result = new BehaviourFrame[m.instructions.size()];
		
		// TODO analyze
		
		
		List<String> deps = new ArrayList<String>();
		/**
		 * FIXME obsoleta: non riusciamo ad avere il nome fully qualified del metodo chiamato.
		 * Per avere questa informazione ci serve la classe su cui viene invocato, 
		 * informazione che hanno i Frame.
		 * 
		 * 
		 * for (int i = 0; i < m.instructions.size(); ++i) {
			if (m.instructions.get(i) instanceof InvokeDynamicInsnNode) {
				deps.add(((InvokeDynamicInsnNode) m.instructions.get(i)).name );
				
			}
		}
		 */
		
		for (int i = 0; i < result.length; ++i)
			if (result[i].getInvokedMethod() != null)
				deps.add(result[i].getInvokedMethod());
		
		context.signalDependancy(methodName, deps);

		// TODO
		return null;
	}
}
