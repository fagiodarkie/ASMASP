package com.laneve.asp.ASMAnalysis.asmClasses;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
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
	
	public BehaviourFrame(Frame<? extends AnValue> src, String methodName, AnalysisContext context) {
		super(src);
		this.context = context;
		this.methodName = methodName;
	}
	
	public String getInvokedMethod() {
		// TODO
		return "";
	}

	public BehaviourFrame(int a, int b) {
		super(a, b);
		for (int i = 0; i < getLocals(); ++i)
			setLocal(i, null);
	}

	public ThreadResource getBehaviour() {
		return frameBehaviour;
	}

	@Override
	public void execute(final AbstractInsnNode insn,
			final Interpreter<AnValue> interpreter) throws AnalyzerException {


		// execute the instruction ?
		// super.execute(insn, interpreter);

		// update the behaviour.
		switch (insn.getOpcode()) {
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
			super.execute(insn, interpreter);
		case Opcodes.ILOAD:
			// If the local is null, we load a VarIntExpression - the method is loading a parameter.
			if (getLocal(((VarInsnNode) insn).var) != null)
				push(interpreter.copyOperation(insn,
                    getLocal(((VarInsnNode) insn).var)));
			else push(new VarExpression(Type.INT_TYPE, ((VarInsnNode) insn).var));
		case Opcodes.LLOAD:
			// If the local is null, we load a VarIntExpression - the method is loading a parameter.
			if (getLocal(((VarInsnNode) insn).var) != null)
				push(interpreter.copyOperation(insn,
                    getLocal(((VarInsnNode) insn).var)));
			else push(new VarExpression(Type.LONG_TYPE, ((VarInsnNode) insn).var));
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
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
			context.setReturnExpression(this.methodName, pop());
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
		case Opcodes.RETURN:
		case Opcodes.LCMP:
		case Opcodes.FCMPG:
		case Opcodes.DCMPG:
		case Opcodes.FCMPL:
		case Opcodes.DCMPL:
		case Opcodes.IFEQ:
		case Opcodes.IFNE:
		case Opcodes.IFLT:
		case Opcodes.IFGE:
		case Opcodes.IFGT:
		case Opcodes.IFLE:
		case Opcodes.IF_ICMPEQ:
		case Opcodes.IF_ICMPNE:
		case Opcodes.IF_ICMPLT:
		case Opcodes.IF_ICMPGE:
		case Opcodes.IF_ICMPGT:
		case Opcodes.IF_ICMPLE:
		case Opcodes.IF_ACMPEQ:
		case Opcodes.IF_ACMPNE:
		case Opcodes.IFNULL:
		case Opcodes.IFNONNULL:
		case Opcodes.JSR:
		case Opcodes.RET:
		case Opcodes.TABLESWITCH:
		case Opcodes.LOOKUPSWITCH:
		case Opcodes.GETSTATIC:
		case Opcodes.PUTSTATIC:
		case Opcodes.GETFIELD:
		case Opcodes.PUTFIELD:
		case Opcodes.INVOKEVIRTUAL:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKESTATIC:
		case Opcodes.INVOKEINTERFACE:
		case Opcodes.INVOKEDYNAMIC:
			// TODO qui dobbiamo avere informazioni sui metodi invocati in questo punto. In ogni frame teniamoci segnato l'eventuale metodo chiamato.
		case Opcodes.ATHROW:
		case Opcodes.CHECKCAST:
		case Opcodes.INSTANCEOF:
		case Opcodes.MONITORENTER:
		case Opcodes.MONITOREXIT:
		default:
			throw new RuntimeException("Illegal opcode " + insn.getOpcode());
		}
	}


	protected boolean isInScope(String methodName) {
		// TODO Auto-generated method stub
		return false;
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
