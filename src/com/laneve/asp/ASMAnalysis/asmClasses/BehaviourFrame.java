package com.laneve.asp.ASMAnalysis.asmClasses;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.bTypes.Atom;
import com.laneve.asp.ASMAnalysis.bTypes.BranchingBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ChainedBehavior;
import com.laneve.asp.ASMAnalysis.bTypes.FinalBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.SimpleBehaviour;


public class BehaviourFrame extends Frame<AnValue> {

	protected IBehaviour frameBehaviour;
	private String deallocationSignature;
	private String allocationSignature;
	
	protected static int getOpType(int opcode) {
		switch(opcode) {
		case Opcodes.NOP:
		case Opcodes.ACONST_NULL:
		case Opcodes.ICONST_M1:
		case Opcodes.ICONST_0:
		case Opcodes.ICONST_1:
		case Opcodes.ICONST_2:
		case Opcodes.ICONST_3:
		case Opcodes.ICONST_4:
		case Opcodes.ICONST_5:
		case Opcodes.LCONST_0:
		case Opcodes.LCONST_1:
		case Opcodes.FCONST_0:
		case Opcodes.FCONST_1:
		case Opcodes.FCONST_2:
		case Opcodes.DCONST_0:
		case Opcodes.DCONST_1:
		case Opcodes.BIPUSH:
		case Opcodes.SIPUSH:
		case Opcodes.LDC:
		case Opcodes.ILOAD:
		case Opcodes.LLOAD:
		case Opcodes.FLOAD:
		case Opcodes.DLOAD:
		case Opcodes.ALOAD:
		case Opcodes.IALOAD:
		case Opcodes.LALOAD:
		case Opcodes.FALOAD:
		case Opcodes.DALOAD:
		case Opcodes.AALOAD:
		case Opcodes.BALOAD:
		case Opcodes.CALOAD:
		case Opcodes.SALOAD:
		case Opcodes.ISTORE:
		case Opcodes.LSTORE:
		case Opcodes.FSTORE:
		case Opcodes.DSTORE:
		case Opcodes.ASTORE:
		case Opcodes.IASTORE:
		case Opcodes.LASTORE:
		case Opcodes.FASTORE:
		case Opcodes.DASTORE:
		case Opcodes.AASTORE:
		case Opcodes.BASTORE:
		case Opcodes.CASTORE:
		case Opcodes.SASTORE:
		case Opcodes.POP:
		case Opcodes.POP2:
		case Opcodes.DUP:
		case Opcodes.DUP_X1:
		case Opcodes.DUP_X2:
		case Opcodes.DUP2:
		case Opcodes.DUP2_X1:
		case Opcodes.DUP2_X2:
		case Opcodes.SWAP:
		case Opcodes.IADD:
		case Opcodes.LADD:
		case Opcodes.FADD:
		case Opcodes.DADD:
		case Opcodes.ISUB:
		case Opcodes.LSUB:
		case Opcodes.FSUB:
		case Opcodes.DSUB:
		case Opcodes.IMUL:
		case Opcodes.LMUL:
		case Opcodes.FMUL:
		case Opcodes.DMUL:
		case Opcodes.IDIV:
		case Opcodes.LDIV:
		case Opcodes.FDIV:
		case Opcodes.DDIV:
		case Opcodes.IREM:
		case Opcodes.LREM:
		case Opcodes.FREM:
		case Opcodes.DREM:
		case Opcodes.INEG:
		case Opcodes.LNEG:
		case Opcodes.FNEG:
		case Opcodes.DNEG:
		case Opcodes.ISHL:
		case Opcodes.LSHL:
		case Opcodes.ISHR:
		case Opcodes.LSHR:
		case Opcodes.IUSHR:
		case Opcodes.LUSHR:
		case Opcodes.IAND:
		case Opcodes.LAND:
		case Opcodes.IOR:
		case Opcodes.LOR:
		case Opcodes.IXOR:
		case Opcodes.LXOR:
		case Opcodes.IINC:
		case Opcodes.I2L:
		case Opcodes.I2F:
		case Opcodes.I2D:
		case Opcodes.L2I:
		case Opcodes.L2F:
		case Opcodes.L2D:
		case Opcodes.F2I:
		case Opcodes.F2L:
		case Opcodes.F2D:
		case Opcodes.D2I:
		case Opcodes.D2L:
		case Opcodes.D2F:
		case Opcodes.I2B:
		case Opcodes.I2C:
		case Opcodes.I2S:
		case Opcodes.NEW:
		case Opcodes.NEWARRAY:
		case Opcodes.ANEWARRAY:
		case Opcodes.ARRAYLENGTH:
		case Opcodes.MULTIANEWARRAY:
		case Opcodes.GOTO:
			return Opcodes.NOP;
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
		case Opcodes.RETURN:
			return Opcodes.RETURN;



		default:
			return opcode;
			
		
		}
	}
	
	public BehaviourFrame(Frame<? extends AnValue> src) {
		super(src);
	}

	public BehaviourFrame(int a, int b) {
		super(a, b);
	}

	public IBehaviour getBehaviour() {
		return frameBehaviour;
	}

	protected List<AnValue> getMemoryAsList() {
		List<AnValue> res = new ArrayList<AnValue>();

		for (int i = 0; i < getLocals(); ++i)
			res.add(getLocal(i));
		for (int i = 0; i < getStackSize(); ++i)
			res.add(getStack(i));

		return res;
	}

	protected void rebaseValues() {
		int index = 0;
		// redefine local variables with formal names
		for (; index < getLocals(); ++index) {
			AnValue v = getLocal(index);
			v.setInternalValue("x" + index);
			setLocal(index, v);
		}
		List<AnValue> tempStack = new ArrayList<AnValue>();
		//backup the stack
		for (int i = 0; i < getStackSize(); ++i) {
			AnValue v = getStack(index);
			v.setInternalValue("x" + index + i);
			tempStack.add(v);
		}
		// empty the stack
		for (int i = 0; i < getStackSize(); ++i)
			pop();
		// refill the stack with formal names
		for (int i = 0; i < tempStack.size(); ++i)
			push(tempStack.get(i));
	}
	
	@Override
	public void execute(final AbstractInsnNode insn,
			final Interpreter<AnValue> interpreter) throws AnalyzerException {
		AnValue value1, value2, value3, value4;
		List<AnValue> values;

		rebaseValues();
		// now the local variables are like: x1:int, x2:int, x3:float

		// TODO dummy values
		int lineStart = 0, lineTarget1 = 1, lineTarget2 = 2;
		String methodNameStart = "org.dummy.start", methodNameTarget = "org.dummy.target";
		
		// save a picture of the status pre-execution.
		List<AnValue> startList = getMemoryAsList();

		// execute the instruction
		super.execute(insn, interpreter);

		// create temp variables for jump.
		AnValue condVar = getStack(getStackSize() - 1);
		String testedValue1 = "x" + (startList.size() - 1),
				testedValue2 = "x" + startList.size();
		List<AnValue> endList = getMemoryAsList();
		List<String> condList = new ArrayList<String>();
		List<SimpleBehaviour> behaviourList = new ArrayList<SimpleBehaviour>();
		int index = endList.size();

		// update the behaviour.
		switch (getOpType(insn.getOpcode())) {
		case Opcodes.NOP:
			/* All these operations are "linear": no jump or conditional is required.
			 * Just take a picture before and after the operation, and put together a
			 * simple behavior with the necessary info.
			 * 
			 * GOTO is a non-conditional jump, therefore it's as simple as the other cases.
			 */
			frameBehaviour = new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameStart, lineTarget1, getMemoryAsList());
			break;
		case Opcodes.LCMP:
		case Opcodes.FCMPG:
		case Opcodes.DCMPG:
		case Opcodes.FCMPL:
		case Opcodes.DCMPL:
			lineTarget2 = lineStart + 1;
			// remove the top-level variable (we need to change its value)
			pop();
			if (insn.getOpcode() == Opcodes.DCMPL
				|| insn.getOpcode() == Opcodes.FCMPL) {
				// fill the condition list;
				// when the check is for less than, invert condition order.
				condList.add(testedValue1 + " > " + testedValue2);
				condList.add(testedValue1 + " = " + testedValue2);
				condList.add(testedValue1 + " < " + testedValue2);
			} else {
				condList.add(testedValue1 + " < " + testedValue2);
				condList.add(testedValue1 + " = " + testedValue2);
				condList.add(testedValue1 + " > " + testedValue2);				
			}
			
			// set the value of the top-level variable and add it again, then change it for next branches.
			condVar.setInternalValue("-1");
			endList.add(condVar);
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			condVar.setInternalValue("0");
			endList.set(index, condVar);
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			condVar.setInternalValue("1");
			endList.set(index, condVar);
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			// finally, create the branching behaviour with respective behaviours and conditions
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IFEQ:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue2 + " != 0");		
			condList.add(testedValue2 + " = 0");
			// instead, we must modify the line target.
			// TODO find out how. what if bigEndian / littleEndian makes difference?
			// (parseint(value1.getValue()) << 8) + parseInt(value2.getValue())
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IFNE:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue2 + " = 0");
			condList.add(testedValue2 + " != 0");		
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IFLT:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue2 + " >= 0");		
			condList.add(testedValue2 + " < 0");
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IFGE:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue2 + " < 0");
			condList.add(testedValue2 + " >= 0");		
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IFGT:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue2 + " <= 0");		
			condList.add(testedValue2 + " > 0");
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IFLE:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue2 + " > 0");
			condList.add(testedValue2 + " <= 0");		
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ICMPEQ:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue1 + " != " + testedValue2);		
			condList.add(testedValue1 + " = " + testedValue2);
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ICMPNE:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue1 + " = " + testedValue2);
			condList.add(testedValue1 + " != " + testedValue2);		
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ICMPLT:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue1 + " >= " + testedValue2);		
			condList.add(testedValue1 + " < " + testedValue2);
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ICMPGE:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue1 + " < " + testedValue2);
			condList.add(testedValue1 + " >= " + testedValue2);		
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ICMPGT:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue1 + " <= " + testedValue2);		
			condList.add(testedValue1 + " > " + testedValue2);
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ICMPLE:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			condList.add(testedValue1 + " > " + testedValue2);
			condList.add(testedValue1 + " <= " + testedValue2);		
			// instead, we must modify the line target.
			// TODO find out how.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
			break;
		case Opcodes.IF_ACMPEQ:
		case Opcodes.IF_ACMPNE:
		case Opcodes.IFNULL:
		case Opcodes.IFNONNULL:
			lineTarget1 = lineStart + 3;
			// here no value is added: the program jumps to the other label directly.
			// instead, we must modify the line target.
			// TODO find out how.
			// reference / null check is (for the time being) not a valid condition: using null cond list.
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget1, endList));
			behaviourList.add(new SimpleBehaviour(methodNameStart, lineStart, startList,
					methodNameTarget, lineTarget2, endList));
			frameBehaviour = new BranchingBehaviour(behaviourList, null);
			break;
		case Opcodes.JSR:
			/* TODO this is a "jump" in a subroutine defined by the values v1 and v2: may be implemented
			 * once we understand how it works.
			 * example:
			 * 
			 * file.method1.45(a, b, c) = file.methodX.0(a, b, c, returnAddress)
			 * ! return address is pushed on the stack, and will be immediately stored by subroutine.
			 * return address would be file.method1.48
			 * 
			 * 
			 * ret is dual, as it takes a value from the stack and goes to that address.
			 */
		case Opcodes.RET:
		case Opcodes.TABLESWITCH:
		case Opcodes.LOOKUPSWITCH:			
			break;
			
		case Opcodes.RETURN:
			frameBehaviour = new FinalBehaviour(methodNameStart, lineStart, startList);
			break;
		case Opcodes.GETSTATIC:
		case Opcodes.PUTSTATIC:
		case Opcodes.GETFIELD:
		case Opcodes.PUTFIELD:
			// TODO we need (?) a way to handle static and non-static fields. 
			break;
		case Opcodes.INVOKEVIRTUAL:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKESTATIC:
		case Opcodes.INVOKEINTERFACE:
		case Opcodes.INVOKEDYNAMIC:
			// TODO infer which method was called.
			String methodName = "dummy";
			
			if (methodName == allocationSignature)
				frameBehaviour = new ChainedBehavior(Atom.ACQUIRE, methodNameStart, lineStart, startList,
						methodNameTarget, lineTarget1, endList);
				
			break;
		case Opcodes.ATHROW:
		case Opcodes.CHECKCAST:
		case Opcodes.INSTANCEOF:
		case Opcodes.MONITORENTER:
		case Opcodes.MONITOREXIT:
			// TODO kind of branches, but we can't tell where do they jump
			break;
		default:
			throw new RuntimeException("Illegal opcode " + insn.getOpcode());
		}
	}


	public void setAllocationSignature(String sig) {
		allocationSignature = sig;
	}
	
	public void setDeallocationSignature(String sig) {
		deallocationSignature = sig;
	}

	/*
	 * 
	 * eg: in IADD
	 * 
	 * 1) compute list of rebased AnValues (values become "x_i")
	 * value2 = "x" + getstacksize
	 * value1 = "x" + getstacksize - 1
	 * 2) save it
	 * 3) super.visit
	 * 4) compute list of new AnValues (keeping values of the kind "x_i + x_j")
	 * 5) save it
	 * 6) backup the relevant Behavioural type from the lists.
	 * 
	 * TODO: understand how line index works.
	 * 
	 * in interpreter:
	 * return value1.value "+" value2.value
	 * 
	 */

}
