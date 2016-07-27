package com.laneve.asp.ASMAnalysis.asmClasses;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.VarExpression;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;


public class BehaviourFrame extends Frame<AnValue> {

	
	/*
	 * TODO: behaviour for method calls is 0 unless methods are typed. In which case..?
	 */
	
	protected ThreadResource frameBehaviour;
	protected AnalysisContext context;
	protected String methodName;
	private String invokedMethod;
	
	public BehaviourFrame(Frame<? extends AnValue> src, String methodName, AnalysisContext context) {
		super(src);
		this.context = context;
		this.methodName = methodName;
		invokedMethod = null;
	}
	
	public String getInvokedMethod() {
		return invokedMethod;
	}

	public BehaviourFrame(int a, int b) {
		super(a, b);
		for (int i = 0; i < getLocals(); ++i)
			setLocal(i, null);
		invokedMethod = null;
	}
	
	public void addAnalysisInformations(String methodName, AnalysisContext context) {
		this.methodName = methodName;
		this.context = context;
	}

	public ThreadResource getBehaviour() {
		return frameBehaviour;
	}

	@Override
	public void execute(final AbstractInsnNode insn,
			final Interpreter<AnValue> interpreter) throws AnalyzerException {
		
		// we only redefine the opcodes which behaviour differs from the standard.

		switch (insn.getOpcode()) {
		case Opcodes.ILOAD:
			// If the local is null, we load a VarIntExpression - the method is loading a parameter.
			if (getLocal(((VarInsnNode) insn).var) != null)
				push(interpreter.copyOperation(insn,
                    getLocal(((VarInsnNode) insn).var)));
			else push(new VarExpression(Type.INT_TYPE, ((VarInsnNode) insn).var));
			break;
		case Opcodes.LLOAD:
			// If the local is null, we load a VarIntExpression - the method is loading a parameter.
			if (getLocal(((VarInsnNode) insn).var) != null)
				push(interpreter.copyOperation(insn,
                    getLocal(((VarInsnNode) insn).var)));
			else push(new VarExpression(Type.LONG_TYPE, ((VarInsnNode) insn).var));
			break;
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
		case Opcodes.RETURN:
			((ValInterpreter)interpreter).setCurrentMethod(methodName);
			super.execute(insn, interpreter);
			break;
		case Opcodes.INVOKEVIRTUAL:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKESTATIC:
		case Opcodes.INVOKEINTERFACE:
		case Opcodes.INVOKEDYNAMIC:
			// TODO
			if (insn.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {	
				InvokeDynamicInsnNode invoke = ((InvokeDynamicInsnNode) insn);
				// TODO
				invokedMethod = "." + invoke.name + invoke.desc;
			} else if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode invoke = ((MethodInsnNode) insn);
				invokedMethod = invoke.owner + "." + invoke.name + invoke.desc;				
			}
			((ValInterpreter)interpreter).setCurrentMethod(invokedMethod);
			super.execute(insn, interpreter);
			break;
		default:
			if (insn.getOpcode() < 200) super.execute(insn, interpreter);
			else throw new RuntimeException("Illegal opcode " + insn.getOpcode());
		}
	}

	protected boolean isInScope(String methodName) {
		// TODO Auto-generated method stub
		return false;
	}

}
