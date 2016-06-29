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
import com.laneve.asp.ASMAnalysis.bTypes.BranchingBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.SimpleBehaviour;


public class BehaviourFrame extends Frame<AnValue> {

	protected IBehaviour frameBehaviour;
	
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
		int var;

		rebaseValues();
		// now the local variables are like: x1:int, x2:int, x3:float

		// TODO dummy values
		int lineStart = 0, lineTarget1 = 1, lineTarget2 = 2;
		String methodNameStart = "org.dummy.start", methodNameTarget = "org.dummy.target";
		
		// save a picture of the status pre-execution.
		List<AnValue> startList = getMemoryAsList();

		// execute the instruction
		super.execute(insn, interpreter);

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
			AnValue condVar = pop();
			String testedValue1 = "x" + (startList.size() - 1),
					testedValue2 = "x" + startList.size();
			List<AnValue> endList = getMemoryAsList();
			List<String> condList = new ArrayList<String>();
			condList.add(testedValue1 + " < " + testedValue2);
			condList.add(testedValue1 + " = " + testedValue2);
			condList.add(testedValue1 + " > " + testedValue2);
			
			List<SimpleBehaviour> behaviourList = new ArrayList<SimpleBehaviour>();
			condVar.setInternalValue("-1");
			int index = endList.size();
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
			frameBehaviour = new BranchingBehaviour(behaviourList, condList);
		case Opcodes.FCMPL:
		case Opcodes.FCMPG:
		case Opcodes.DCMPL:
		case Opcodes.DCMPG:
			AnValue cond = pop();
			break;
		case Opcodes.IFEQ:
		case Opcodes.IFNE:
		case Opcodes.IFLT:
		case Opcodes.IFGE:
		case Opcodes.IFGT:
		case Opcodes.IFLE:
			interpreter.unaryOperation(insn, pop());
			break;
		case Opcodes.IF_ICMPEQ:
		case Opcodes.IF_ICMPNE:
		case Opcodes.IF_ICMPLT:
		case Opcodes.IF_ICMPGE:
		case Opcodes.IF_ICMPGT:
		case Opcodes.IF_ICMPLE:
		case Opcodes.IF_ACMPEQ:
		case Opcodes.IF_ACMPNE:
			value2 = pop();
			value1 = pop();
			interpreter.binaryOperation(insn, value1, value2);
			break;
		case Opcodes.JSR:
			push(interpreter.newOperation(insn));
			break;
		case Opcodes.RET:
			break;
		case Opcodes.TABLESWITCH:
		case Opcodes.LOOKUPSWITCH:
			interpreter.unaryOperation(insn, pop());
			break;
		case Opcodes.RETURN:
			// TODO finalBehaviour.
			break;
		case Opcodes.GETSTATIC:
			push(interpreter.newOperation(insn));
			break;
		case Opcodes.PUTSTATIC:
			interpreter.unaryOperation(insn, pop());
			break;
		case Opcodes.GETFIELD:
			push(interpreter.unaryOperation(insn, pop()));
			break;
		case Opcodes.PUTFIELD:
			value2 = pop();
			value1 = pop();
			interpreter.binaryOperation(insn, value1, value2);
			break;
		case Opcodes.INVOKEVIRTUAL:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKESTATIC:
		case Opcodes.INVOKEINTERFACE: {
			values = new ArrayList<AnValue>();
			String desc = ((MethodInsnNode) insn).desc;
			for (int i = Type.getArgumentTypes(desc).length; i > 0; --i) {
				values.add(0, pop());
			}
			if (insn.getOpcode() != Opcodes.INVOKESTATIC) {
				values.add(0, pop());
			}
			if (Type.getReturnType(desc) == Type.VOID_TYPE) {
				interpreter.naryOperation(insn, values);
			} else {
				push(interpreter.naryOperation(insn, values));
			}
			break;
		}
		case Opcodes.INVOKEDYNAMIC: {
			values = new ArrayList<AnValue>();
			String desc = ((InvokeDynamicInsnNode) insn).desc;
			for (int i = Type.getArgumentTypes(desc).length; i > 0; --i) {
				values.add(0, pop());
			}
			if (Type.getReturnType(desc) == Type.VOID_TYPE) {
				interpreter.naryOperation(insn, values);
			} else {
				push(interpreter.naryOperation(insn, values));
			}
			break;
		}
		case Opcodes.NEW:
			push(interpreter.newOperation(insn));
			break;
		case Opcodes.NEWARRAY:
		case Opcodes.ANEWARRAY:
		case Opcodes.ARRAYLENGTH:
			push(interpreter.unaryOperation(insn, pop()));
			break;
		case Opcodes.ATHROW:
			interpreter.unaryOperation(insn, pop());
			break;
		case Opcodes.CHECKCAST:
		case Opcodes.INSTANCEOF:
			push(interpreter.unaryOperation(insn, pop()));
			break;
		case Opcodes.MONITORENTER:
		case Opcodes.MONITOREXIT:
			interpreter.unaryOperation(insn, pop());
			break;
		case Opcodes.MULTIANEWARRAY:
			values = new ArrayList<AnValue>();
			for (int i = ((MultiANewArrayInsnNode) insn).dims; i > 0; --i) {
				values.add(0, pop());
			}
			push(interpreter.naryOperation(insn, values));
			break;
		case Opcodes.IFNULL:
		case Opcodes.IFNONNULL:
			interpreter.unaryOperation(insn, pop());
			break;
		default:
			throw new RuntimeException("Illegal opcode " + insn.getOpcode());
		}
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
