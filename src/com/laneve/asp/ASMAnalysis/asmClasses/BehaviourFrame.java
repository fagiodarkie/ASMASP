package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.bTypes.Atom;
import com.laneve.asp.ASMAnalysis.bTypes.ConditionalJump;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;


public class BehaviourFrame extends Frame<AnValue> {
	
	protected IBehaviour frameBehaviour;
	protected AnalysisContext context;
	protected String methodName, methodParametersPattern;
	protected String invokedMethod;
	
	public BehaviourFrame(Frame<? extends AnValue> src, String methodName, AnalysisContext context) {
		super(src);
		this.context = context;
		this.methodName = methodName;
		invokedMethod = null;
		methodParametersPattern = "";
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
	}
	
	public BehaviourFrame(BehaviourFrame src) {
		super(src);
		addAnalysisInformations(src.methodName, src.context);
		invokedMethod = src.invokedMethod;
		if (src.frameBehaviour != null)
			frameBehaviour = src.frameBehaviour.clone();
		else frameBehaviour = null;
		methodParametersPattern = src.methodParametersPattern;
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
		
		frameBehaviour = null;
		invokedMethod = null;
		
		ValInterpreter in = (ValInterpreter) interpreter; 
		// we only redefine the opcodes which behaviour differs from the standard.

		switch (insn.getOpcode()) {

		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
		case Opcodes.RETURN:
			in.setCurrentMethod(methodName, methodParametersPattern);
			super.execute(insn, in);
			frameBehaviour = new Atom(Atom.RETURN);//in.getBehaviour();
			
			List<AnValue> localList = new ArrayList<AnValue>();
			for (int i = 0; i < getLocals(); ++i)
				localList.add(getLocal(i));
			context.signalFinalState(methodName, localList);
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
			in.setCurrentMethod(invokedMethod, methodParametersPattern);
			super.execute(insn, in);
			
			if (in.getBehaviour() != null) {
				frameBehaviour = in.getBehaviour().clone();
				
				if (frameBehaviour instanceof ThreadResource) {
					ThreadResource t = (ThreadResource)frameBehaviour;
					ThreadValue thr = t.getThreadValue();
					int status = ThreadResource.ALREADY_RELEASED;
					if (!t.isRelease())
						status = ThreadResource.ALREADY_ACQUIRED;
					if (thr.isVariable())
						context.signalNewStatus(methodName, methodParametersPattern, thr.getVariableName(), status);						
//					System.out.println("BFrame: updating after call of " + methodName + " thread #" + thr.getThreadID() + " with new status: " + status);
					updateThreadStatus(thr.getThreadID(), status);
				}

				else {
					for (Entry<Long, Map<String, AnValue>> entry : in.getUpdates().entrySet())
						updateByID(entry.getKey(), entry.getValue());
					for (Entry<Long, Integer> e : in.getUpdatedThreads().entrySet()) {
						updateThreadStatus(e.getKey(), e.getValue());
					}
				}
			}
			
			in.resetCurrentMethod();
					
			break;
		case Opcodes.PUTFIELD:
			super.execute(insn, interpreter);
			AnValue newObject = in.getCurrentObject();
			updateByID(newObject);
            in.resetCurrentMethod();
			break;
		default:
			if (insn.getOpcode() < 200) super.execute(insn, in);
			else throw new RuntimeException("Illegal opcode " + insn.getOpcode());
		}
	}

	protected void updateThreadStatus(Long long1, Integer value) {
		for (int i = 0; i < getLocals(); ++i)
			if (getLocal(i) instanceof ThreadValue) {
				if (((ThreadValue)getLocal(i)).getThreadID() == long1)
					((ThreadValue)getLocal(i)).setStatus(value);
			} else updateThreadStatus(getLocal(i), long1, value);
		for (int i = 0; i < getStackSize(); ++i)
			if (getStack(i) instanceof ThreadValue) {
				if (((ThreadValue)getStack(i)).getThreadID() == long1)
					((ThreadValue)getStack(i)).setStatus(value);
			} else updateThreadStatus(getStack(i), long1, value);
	}
	
	protected void updateThreadStatus(AnValue outer, long id, int status) {
		if (outer == null)
			return;
		for (AnValue a: outer.getFields())
			if (a instanceof ThreadValue && ((ThreadValue)a).getThreadID() == id)
				((ThreadValue)a).setStatus(status);
			else updateThreadStatus(a, id, status);
	}


	@Override
	public boolean merge(final Frame<? extends AnValue> other, final Interpreter<AnValue> interpreter) throws AnalyzerException {
		boolean r = super.merge(other, interpreter);
		BehaviourFrame o = (BehaviourFrame)other;
		if (o.frameBehaviour != frameBehaviour) {
			invokedMethod = o.invokedMethod;
			frameBehaviour = (o.frameBehaviour != null ? o.frameBehaviour.clone() : null);
			return true;
		}
		if (o.frameBehaviour == frameBehaviour || o.frameBehaviour.equalBehaviour(frameBehaviour))
			return r;
		
		// frameBehaviour.mergeWith(o.frameBehaviour);
		invokedMethod = o.invokedMethod;
		frameBehaviour = o.frameBehaviour.clone();
		return true;
	}
	
	protected void updateByID(AnValue newValue) {
		updateByID(newValue.getID(), newValue);
	}
	
	protected void updateByID(long ID, Map<String, AnValue> map) {
		
		boolean simpleObject = false;
		
        for (int i = 0; i < getLocals(); ++i) {
        	if (getLocal(i) != null && getLocal(i).getID() == ID) {
        		if (getLocal(i) instanceof ThreadValue || getLocal(i) instanceof IExpression) {
        			simpleObject = true;
        			break;
        		} else
	        		for (Entry<String, AnValue> e : map.entrySet())
	        			getLocal(i).setField(e.getKey(), e.getValue());
        	}
        }
        
        if (!simpleObject)
        	for (int i = 0; i < getStackSize(); ++i) {
		    	if (getStack(i).getID() == ID)
		    		for (Entry<String, AnValue> e : map.entrySet())
		    			getStack(i).setField(e.getKey(), e.getValue());
        }
	}
	
	protected void updateByID(long ID, AnValue newValue) {
		
        for (int i = 0; i < getLocals(); ++i) {
        	if (getLocal(i) != null && getLocal(i).getID() == ID)
        		setLocal(i, newValue);
        }
        
        List<AnValue> tempStack = new ArrayList<AnValue>(); 
        for (int i = 0; i < getStackSize(); ++i) {
        	AnValue t = getStack(i);
        	if (t.getID() == ID)
        		tempStack.add(newValue);
        	else {
        		t.updateByID(ID, newValue);
        		tempStack.add(0, t);
        	}
        }
    	// for a stack A, B, C, tempStack holds C, B, A. pushing restores correct order.
        clearStack();
        for (AnValue a : tempStack)
        	push(a);
	}
	
	public String toString() {
		if (frameBehaviour != null) {
			if (frameBehaviour instanceof ConditionalJump)
				return "jump instruction";
			return frameBehaviour.toString();
		}
		return "";
	}

	public void resetBehaviour() {
		setBehaviour(null);
	}
	
	public BehaviourFrame init(BehaviourFrame src) {
		super.init(src);
		addAnalysisInformations(src.methodName, src.context);
		invokedMethod = src.invokedMethod;
		if (src.frameBehaviour != null)
			frameBehaviour = src.frameBehaviour.clone();
		else frameBehaviour = null;
		methodParametersPattern = src.methodParametersPattern;
		return this;
	}

	public void executeJump(JumpInsnNode j, ValInterpreter in,
			int insn, int sInsn, int jump) throws AnalyzerException {
		in.setJumpLabels(insn + 1, sInsn + 1, jump + 1);
		execute(j, in);
		if (j.getOpcode() == Opcodes.GOTO)
			in.processGOTO(insn, jump + 1);
		frameBehaviour = in.getBehaviour();
		in.resetCurrentMethod();

	}
	
}
