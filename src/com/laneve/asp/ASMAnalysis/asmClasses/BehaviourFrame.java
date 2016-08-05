package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.List;

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
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.ConstExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.VarExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.EqExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.FalseExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.GeExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.GtExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.IBoolExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.LeExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.LtExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.NeExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.NotExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.TrueExpression;
import com.laneve.asp.ASMAnalysis.bTypes.ConditionalJump;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;


public class BehaviourFrame extends Frame<AnValue> {
	
	protected IBehaviour frameBehaviour;
	protected AnalysisContext context;
	protected String methodName;
	protected String invokedMethod, methodParametersPattern;
	protected boolean resetBehaviour;
	
	public BehaviourFrame(Frame<? extends AnValue> src, String methodName, AnalysisContext context) {
		super(src);
		this.context = context;
		this.methodName = methodName;
		invokedMethod = null;
		methodParametersPattern = "";
		resetBehaviour = true;
	}
	
	public void setParameterPattern(String s) {
		methodParametersPattern = s;
	}
	
	public String getInvokedMethod() {
		return invokedMethod;
	}

	public BehaviourFrame(int a, int b) {
		super(a, b);
		for (int i = 0; i < getLocals(); ++i)
			setLocal(i, null);
		invokedMethod = null;
		methodParametersPattern = "";
		resetBehaviour = true;
		
	}
	
	public BehaviourFrame(BehaviourFrame src) {
		super(src);
		addAnalysisInformations(src.methodName, src.context);
		invokedMethod = src.invokedMethod;
		if (src.frameBehaviour != null)
			frameBehaviour = src.frameBehaviour.clone();
		else frameBehaviour = null;
		methodParametersPattern = src.methodParametersPattern;
		resetBehaviour = true;
	}

	public void addAnalysisInformations(String methodName, AnalysisContext context) {
		this.methodName = methodName;
		this.context = context;
	}

	public void setBehaviour(IBehaviour b) {
		frameBehaviour = b;
	}
	
	public IBehaviour getBehaviour() {
		return frameBehaviour;
	}

	@Override
	public void execute(final AbstractInsnNode insn,
			final Interpreter<AnValue> interpreter) throws AnalyzerException {
		
		if (resetBehaviour) {
			frameBehaviour = null;
			invokedMethod = null;
			resetBehaviour = false;
		}
		
		ValInterpreter in = (ValInterpreter) interpreter; 
		// we only redefine the opcodes which behaviour differs from the standard.

		switch (insn.getOpcode()) {

		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
		case Opcodes.RETURN:
			in.setCurrentMethod(methodName);
			super.execute(insn, in);
			in.resetCurrentMethod();
			break;
		case Opcodes.INVOKEVIRTUAL:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKESTATIC:
		case Opcodes.INVOKEINTERFACE:
		case Opcodes.INVOKEDYNAMIC:
			if (insn.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {	
				InvokeDynamicInsnNode invoke = ((InvokeDynamicInsnNode) insn);
				invokedMethod = "." + invoke.name + invoke.desc;
			} else if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode invoke = ((MethodInsnNode) insn);
				invokedMethod = invoke.owner + "." + invoke.name + invoke.desc;				
			}
			in.setCurrentMethod(invokedMethod);
			super.execute(insn, in);
			
			if (in.getBehaviour() != null) {
				frameBehaviour = in.getBehaviour().clone();
				
				if (frameBehaviour instanceof ThreadResource) {
					ThreadResource t = (ThreadResource)frameBehaviour;
					if (t.getThreadValue().isVariable())
						context.signalRelease(methodName, methodParametersPattern, t.getThreadValue().getVariableName());
				}
			}
			in.resetCurrentMethod();
					
			break;
		default:
			if (insn.getOpcode() < 200) super.execute(insn, in);
			else throw new RuntimeException("Illegal opcode " + insn.getOpcode());
		}
	}

	@Override
	public boolean merge(final Frame<? extends AnValue> other, final Interpreter<AnValue> interpreter) throws AnalyzerException {
		boolean r = super.merge(other, interpreter);
		BehaviourFrame o = (BehaviourFrame)other;
		if (o.frameBehaviour.equalBehaviour(frameBehaviour))
			return r;
		
		// frameBehaviour.mergeWith(o.frameBehaviour);
		invokedMethod = o.invokedMethod;
		frameBehaviour = o.frameBehaviour.clone();
		return true;
	}


	public void processJumpInstruction(int insnOpcode, int insn, int i, int jump) {
		resetBehaviour = false;
		Type b = Type.BOOLEAN_TYPE;
		IExpression l, r, zero = new ConstExpression(Type.INT_TYPE, new Long(0));
		IBoolExpression lb, rb, cond;
		switch (insnOpcode) {
		case Opcodes.IFEQ:
			l = (IExpression)getStack(getStackSize() - 1);
			cond = new EqExpression(b, l, zero);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IFNE:
			l = (IExpression)getStack(getStackSize() - 1);
			cond = new NeExpression(b, l, zero);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IFLT:
			l = (IExpression)getStack(getStackSize() - 1);
			cond = new LtExpression(b, l, zero);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IFGE:
			l = (IExpression)getStack(getStackSize() - 1);
			cond = new GeExpression(b, l, zero);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IFGT:
			l = (IExpression)getStack(getStackSize() - 1);
			cond = new GtExpression(b, l, zero);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IFLE:
			l = (IExpression)getStack(getStackSize() - 1);
			cond = new LeExpression(b, l, zero);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IF_ICMPEQ:
			l = (IExpression)getStack(getStackSize() - 1);
			r = (IExpression)getStack(getStackSize() - 2);
			cond = new EqExpression(b, l, r);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IF_ICMPNE:
			l = (IExpression)getStack(getStackSize() - 1);
			r = (IExpression)getStack(getStackSize() - 2);
			cond = new NeExpression(b, l, r);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IF_ICMPLT:
			l = (IExpression)getStack(getStackSize() - 1);
			r = (IExpression)getStack(getStackSize() - 2);
			cond = new LtExpression(b, l, r);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IF_ICMPGE:
			l = (IExpression)getStack(getStackSize() - 1);
			r = (IExpression)getStack(getStackSize() - 2);
			cond = new GeExpression(b, l, r);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IF_ICMPGT:
			l = (IExpression)getStack(getStackSize() - 1);
			r = (IExpression)getStack(getStackSize() - 2);
			cond = new GtExpression(b, l, r);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.IF_ICMPLE:
			l = (IExpression)getStack(getStackSize() - 1);
			r = (IExpression)getStack(getStackSize() - 2);
			cond = new LeExpression(b, l, r);
			frameBehaviour = new ConditionalJump(insn, cond, jump, new NotExpression(b, cond), i);
			break;
		case Opcodes.GOTO:
			frameBehaviour = new ConditionalJump(insn, new TrueExpression(b), jump, new FalseExpression(b), -1);
		case Opcodes.IF_ACMPEQ:
		case Opcodes.IF_ACMPNE:
		case Opcodes.IFNONNULL:
		case Opcodes.IFNULL:
			frameBehaviour = new ConditionalJump(insn, null, jump, null,i);
		}
	}
	
}
