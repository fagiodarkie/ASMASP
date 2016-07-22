package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.ConstExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.DivExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IntAndExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IntOrExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IntXorExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.MinusExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.MulExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.RemExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.SHLExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.SHRExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.SubExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.SumExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.USHRExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.IBoolExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.bools.OrExpression;


public class ValInterpreter extends Interpreter<AnValue> implements Opcodes {

	protected AnalysisContext context;
	
	protected ValInterpreter(int api) {
		super(api);
	}

	public ValInterpreter() {
		super(4);
	}
	
	@Override
	public AnValue newValue(Type type) {
		if (AnValue.isThread(type))
			return context.generateThread();
		return new AnValue(type);
	}

	public void setContext(AnalysisContext analysisContext) {
		context = analysisContext;
	}
	
	@Override
	public AnValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
		AnValue v;
		switch (insn.getOpcode()) {
        case ACONST_NULL:
            return newValue(Type.getObjectType("null"));
        case ICONST_M1:
        case ICONST_0:
        case ICONST_1:
        case ICONST_2:
        case ICONST_3:
        case ICONST_4:
        case ICONST_5:
        	return new ConstExpression(Type.INT_TYPE, AnValue.getConstValue(insn.getOpcode()));
        case LCONST_0:
        case LCONST_1:
        	return new ConstExpression(Type.LONG_TYPE, AnValue.getConstValue(insn.getOpcode()));
        case FCONST_0:
        case FCONST_1:
        case FCONST_2:
        case DCONST_0:
        case DCONST_1:
        	// ?
        case BIPUSH:
        case SIPUSH:
        	return new ConstExpression(Type.LONG_TYPE, AnValue.getConstValue(((IntInsnNode)insn).operand ));
        	
        case LDC:
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Integer) {
                return new ConstExpression(Type.INT_TYPE, AnValue.getConstValue(insn.getOpcode()));
            } else if (cst instanceof Float) {
            	return newValue(Type.FLOAT_TYPE);
            } else if (cst instanceof Long) {
            	return new ConstExpression(Type.LONG_TYPE, AnValue.getConstValue(insn.getOpcode()));
            } else if (cst instanceof Double) {
            	return newValue(Type.DOUBLE_TYPE);
            } else if (cst instanceof String) {
            	// FIXME do we need strings?
                return newValue(Type.getType("java.lang.String"));
            } else if (cst instanceof Type) {
                int sort = ((Type) cst).getSort();
                if (sort == Type.OBJECT || sort == Type.ARRAY) {
                    return newValue(Type.VOID_TYPE);
                } else if (sort == Type.METHOD) {
                    return newValue(Type.VOID_TYPE);
                } else {
                    throw new IllegalArgumentException("Illegal LDC constant "
                            + cst);
                }
            } else if (cst instanceof Handle) {
                return newValue(Type.VOID_TYPE);
            } else {
                throw new IllegalArgumentException("Illegal LDC constant "
                        + cst);
            }
        case GETSTATIC:
        	// TODO maybe rewrite instance of FieldInsnNode to map actual values of fields?
        	throw new Error("Static fields are not analyzed.");
        case NEW:
            return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
        default:
            throw new Error("Internal error.");
        }
	}

	@Override
	public AnValue copyOperation(AbstractInsnNode insn, AnValue value)
			throws AnalyzerException {
		return value.clone();
	}

	@Override
	public AnValue unaryOperation(AbstractInsnNode insn, AnValue value)
			throws AnalyzerException {
		AnValue v = value.clone();
		switch (insn.getOpcode()) {
		case INEG:
		case LNEG:
			return new MinusExpression(value.getType(), ((IExpression) value));
		case IINC:
			return new SumExpression(value.getType(), (IExpression) value, new ConstExpression(Type.INT_TYPE, new Long(1)));
        case Opcodes.I2L:
        	return value;
        case Opcodes.F2L:
        case Opcodes.D2L:
        	return new AnValue(Type.LONG_TYPE);
        case Opcodes.I2F:
        case Opcodes.L2F:
        case Opcodes.D2F:
        case Opcodes.FNEG:
        	return new AnValue(Type.FLOAT_TYPE);
        case Opcodes.L2I:
        case Opcodes.F2I:
        case Opcodes.D2I:
        	return new AnValue(Type.INT_TYPE);
        case Opcodes.I2D:
        case Opcodes.L2D:
        case Opcodes.F2D:
		case Opcodes.DNEG:
        	return new AnValue(Type.DOUBLE_TYPE);
        case Opcodes.I2B:
        	return new AnValue(Type.BOOLEAN_TYPE);
        case Opcodes.I2C:
        	return new AnValue(Type.CHAR_TYPE);
        case Opcodes.I2S:
        	return new AnValue(Type.SHORT_TYPE);
        case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL:
        case Opcodes.IRETURN:
        case Opcodes.LRETURN:
        case Opcodes.FRETURN:
        case Opcodes.DRETURN:
        case Opcodes.ARETURN:
        case Opcodes.TABLESWITCH:
        case Opcodes.LOOKUPSWITCH:
        case Opcodes.PUTSTATIC:
        case Opcodes.GETFIELD:
        	return new AnValue(Type.VOID_TYPE);
        case Opcodes.NEWARRAY:
        	return new AnValue(Type.VOID_TYPE);
        case Opcodes.ANEWARRAY:
        	return new AnValue(Type.VOID_TYPE);
        case Opcodes.ARRAYLENGTH:
        case Opcodes.INSTANCEOF:
        	return new AnValue(Type.INT_TYPE);
        case Opcodes.ATHROW:
        case Opcodes.CHECKCAST:
        case Opcodes.MONITORENTER:
        case Opcodes.MONITOREXIT:
        	// not implemented.
		default:
            throw new Error("Internal error.");
		}
	}

	@Override
	public AnValue binaryOperation(AbstractInsnNode insn, AnValue value1,
			AnValue value2) throws AnalyzerException {
		
		Long v1, v2; 
		switch(insn.getOpcode()) {
		// we don't support array operations. yet.
		case Opcodes.IALOAD:
        case Opcodes.LALOAD:
        case Opcodes.FALOAD:
        case Opcodes.DALOAD:
        case Opcodes.AALOAD:
        case Opcodes.BALOAD:
        case Opcodes.CALOAD:
        case Opcodes.SALOAD:
        	throw new Error("Array operations not supported.");
        // operations are modeled, but only in certain cases.
        case Opcodes.IADD:
        case Opcodes.LADD:
        	return new SumExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.IMUL:
        case Opcodes.LMUL:
        	return new MulExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.ISUB:
        case Opcodes.LSUB:
        	return new SubExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LREM:
        case Opcodes.IREM:
    		return new RemExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.IDIV:
        case Opcodes.LDIV:
        	return new DivExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LOR:
        case Opcodes.IOR:
        	return new IntOrExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LXOR:
        case Opcodes.IXOR:
        	return new IntXorExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LAND:
        case Opcodes.IAND:
        	return new IntAndExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LUSHR:
        case Opcodes.IUSHR:
        	return new USHRExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LSHL:
        case Opcodes.ISHL:
        	return new SHLExpression(value1.getType(), (IExpression) value1, (IExpression) value2);
        case Opcodes.LSHR:
        case Opcodes.ISHR:
        	return new SHRExpression(value1.getType(), (IExpression) value1, (IExpression) value2);

        case Opcodes.FADD:
        case Opcodes.DADD:
        case Opcodes.FMUL:
        case Opcodes.FSUB:
        case Opcodes.FREM:
        case Opcodes.FDIV:
        case Opcodes.DSUB:
        case Opcodes.DREM:
        case Opcodes.DMUL:
        case Opcodes.DDIV:
        	throw new Error("Floating Point operations not supported.");
        case Opcodes.LCMP:
        	v1 = ((IExpression) value1).evaluate();
        	v2 = ((IExpression) value2).evaluate();
        	if (v1 > v2)
        		return new ConstExpression(Type.INT_TYPE, new Long(1));
        	else if (v1 < v2)
        		return new ConstExpression(Type.INT_TYPE, new Long(-1));
        	else return new ConstExpression(Type.INT_TYPE, new Long(0));
        case Opcodes.FCMPL:
        case Opcodes.FCMPG:
    	case Opcodes.DCMPL:
    	case Opcodes.DCMPG:
    		// what do we do with floating point algebras?
        case Opcodes.IF_ICMPEQ:
        case Opcodes.IF_ICMPNE:
        case Opcodes.IF_ICMPLT:
        case Opcodes.IF_ICMPGE:
        case Opcodes.IF_ICMPGT:
        case Opcodes.IF_ICMPLE:
        case Opcodes.IF_ACMPEQ:
        case Opcodes.IF_ACMPNE:
        	// no object is created. jump instruction must be modeled in frame.
        	return null;
        case Opcodes.PUTFIELD:
        
        default:
    		throw new Error("Internal error.");
		}
	}

	@Override
	public AnValue ternaryOperation(AbstractInsnNode insn, AnValue value1,
			AnValue value2, AnValue value3) throws AnalyzerException {
		switch(insn.getOpcode()) {
        case Opcodes.IASTORE:
        case Opcodes.LASTORE:
        case Opcodes.FASTORE:
        case Opcodes.DASTORE:
        case Opcodes.AASTORE:
        case Opcodes.BASTORE:
        case Opcodes.CASTORE:
        case Opcodes.SASTORE:
        	// ternary are always store, which do not produce output.
        	return null;
        default:
    		throw new Error("Internal error.");
		}
	}

	@Override
	public AnValue naryOperation(AbstractInsnNode insn,
			List<? extends AnValue> values) throws AnalyzerException {
		switch(insn.getOpcode()) {
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEDYNAMIC:
        case Opcodes.INVOKEINTERFACE:
        case Opcodes.MULTIANEWARRAY:
        	// undefined, for the time being.
        	// TODO use the context to get the return value of function.
        	
    	default:
    		throw new Error("Internal error.");
    	}
	}

	@Override
	public void returnOperation(AbstractInsnNode insn, AnValue value,
			AnValue expected) throws AnalyzerException {
		// TODO, but probably useless.
		
	}

	@Override
	public AnValue merge(AnValue v, AnValue w) {
		// TODO
		return v;
	}

}
