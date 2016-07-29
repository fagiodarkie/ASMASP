package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;

public class VMAnalyzer extends Analyzer<AnValue> {

	protected AnalysisContext context;
	protected String methodName;

	public VMAnalyzer(Interpreter<AnValue> arg0, AnalysisContext context) {
		super(arg0);
		this.context = context;
	}
	
	@Override
	public BehaviourFrame[] analyze(final String owner, final MethodNode m)
            throws AnalyzerException {
		
		methodName = owner + "." + m.name + m.desc;
		
		/*
		 * The analyze() method is able to compute actual BehaviourFrames, due to
		 * the redefinition of the frame creation methods.
		 * The analysis proceeds according to the BehaviourFrames logic, all we have to do here is
		 * to make sure that actual BehaviourFrames are returned and exploit their additional information.
		 */
		Frame<? extends AnValue>[] temp = super.analyze(owner, m);
		BehaviourFrame[] result = new BehaviourFrame[temp.length];
		IBehaviour b = null;
		for (int i = 0; i < temp.length; ++i) {
			result[i] = (BehaviourFrame) temp[i];
			if (result[i] == null)
				continue;
			if (result[i].frameBehaviour != null && b != null && result[i].frameBehaviour.equal(b))
				result[i].frameBehaviour = null;
			else if (result[i].frameBehaviour != null || b != null) {
				b = (result[i].frameBehaviour == null ? null : result[i].frameBehaviour.clone());
			}
		}
		
		
		List<String> deps = new ArrayList<String>();
		
		for (int i = 0; i < result.length; ++i)
			if (result[i] != null && result[i].getInvokedMethod() != null)
				deps.add(result[i].getInvokedMethod());
		
		context.signalDependancy(methodName, deps);

		return result;
	}
	
	@Override
    protected BehaviourFrame newFrame(final int nLocals, final int nStack) {
		BehaviourFrame res = new BehaviourFrame(nLocals, nStack);
		res.addAnalysisInformations(methodName, context);
        return res;
    }

    @Override
    protected BehaviourFrame newFrame(final Frame<? extends AnValue> src) {
    	BehaviourFrame r = new BehaviourFrame((BehaviourFrame)src);
        return r;
    }

}
