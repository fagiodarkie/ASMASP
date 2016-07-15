package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.ConstExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IBoolExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.MinusExpression;


public class ValInterpreter extends Interpreter<AnValue> implements Opcodes {

	protected AnalysisContext context;
	
	protected ValInterpreter(int api) {
		super(api);
	}

	@Override
	public AnValue newValue(Type type) {
/*		switch(type.getSort()) {
		case Type.INT:
		case Type.LONG:
			return new IExpression(type);
		case Type.BOOLEAN:
			return new IBoolExpression(type);
		case Type.OBJECT:
			if (AnValue.getClassName(type) == AnValue.THREAD_NAME)
				return context.generateThread();
		}*/
		
		return new AnValue(type);
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
        	// FIXME BIPUSH and SIPUSH should cast a byte or short to int32, but no value to be cast is provided.
        	return newValue(Type.INT_TYPE);
        	
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
			return new IntegerValue(new MinusExpression(((IntegerValue) value).getValue()))
		case IINC:
			if (value.getExpType() != ExpressionType.UNDEFINED_EXP
			&& value.getExpType() != ExpressionType.UNKNOWN) {
				// NB value is integer, because otherwise the bytecode would be wrong.
				v.setInternalValue("(" + value.getValue() + " + 1)");
				return v;
			} else return value;
        case Opcodes.I2L:
        case Opcodes.F2L:
        case Opcodes.D2L:
        	v.setClassName(AnValue.LONG_NAME);
        	return v;
        case Opcodes.I2F:
        case Opcodes.L2F:
        case Opcodes.D2F:
        	v.setClassName(AnValue.FLOAT_NAME);
        	return v;
        case Opcodes.L2I:
        case Opcodes.F2I:
        case Opcodes.D2I:
        	v.setClassName(AnValue.INT_NAME);
        	return v;
        case Opcodes.I2D:
        case Opcodes.L2D:
        case Opcodes.F2D:
        	v.setClassName(AnValue.DOUBLE_NAME);
        	return v;
        case Opcodes.I2B:
        	v.setClassName(AnValue.BOOL_NAME);
        	return v;
        case Opcodes.I2C:
        	v.setClassName(AnValue.CHAR_NAME);
        	return v;
        case Opcodes.I2S:
        	v.setClassName(AnValue.SHORT_NAME);
        	return v;
        case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL:
        	// TODO jumps in interpreter. change the frame's behavioural types?
        	// no, jumps must be computed in Frame.
        case Opcodes.IRETURN:
        case Opcodes.LRETURN:
        case Opcodes.FRETURN:
        case Opcodes.DRETURN:
        case Opcodes.ARETURN:
        	// TODO behaviour types should be "0", will be computed in Frame.
        case Opcodes.TABLESWITCH:
        case Opcodes.LOOKUPSWITCH:
        case Opcodes.PUTSTATIC:
        case Opcodes.GETFIELD:
        case Opcodes.NEWARRAY:
        case Opcodes.ANEWARRAY:
        case Opcodes.ARRAYLENGTH:
        case Opcodes.ATHROW:
        case Opcodes.CHECKCAST:
        case Opcodes.INSTANCEOF:
        case Opcodes.MONITORENTER:
        case Opcodes.MONITOREXIT:
		case DNEG:
		case FNEG:
        	// not implemented.
		default:
            throw new Error("Internal error.");
		}
	}

	@Override
	public AnValue binaryOperation(AbstractInsnNode insn, AnValue value1,
			AnValue value2) throws AnalyzerException {
		AnValue unknown = new AnValue(Type.INT_TYPE);
		unknown.setExpressionType(ExpressionType.UNKNOWN);
    	AnValue res = new AnValue(Type.INT_TYPE);
		switch(insn.getOpcode()) {
		// we don't support array operations. yet.
		case Opcodes.IALOAD:
			return unknown;
        case Opcodes.LALOAD:
        	unknown.setClassName(AnValue.LONG_NAME);
        	return unknown;
        case Opcodes.FALOAD:
        	unknown.setClassName(AnValue.FLOAT_NAME);
        	return unknown;
        case Opcodes.DALOAD:
        	unknown.setClassName(AnValue.DOUBLE_NAME);
        	return unknown;
        case Opcodes.AALOAD:
        	unknown.setClassName(AnValue.REF_NAME);
        	return unknown;
        case Opcodes.BALOAD:
        	unknown.setClassName(AnValue.BOOL_NAME);
        	return unknown;
        case Opcodes.CALOAD:
        	unknown.setClassName(AnValue.CHAR_NAME);
        	return unknown;
        case Opcodes.SALOAD:
        	unknown.setClassName(AnValue.SHORT_NAME);
        	return unknown;
        // operations are modeled, but only in certain cases.
        case Opcodes.IADD:
        case Opcodes.LADD:
        case Opcodes.FADD:
        case Opcodes.DADD:
        	// we assert both addends are the same type.
        	if (value1.defined() && value2.defined()) {
        		// x1 + x2
        		res.setExpressionValue("(" + value1.getValue() + " + " + value2.getValue() + ")",
        			AnValue.leastUpperBound(value1.getExpType(), value2.getExpType()));
        		if (res.getExpType() == ExpressionType.CONST)
        			res.setExpressionType(ExpressionType.CONST_EXP);
        		else if (res.getExpType() == ExpressionType.VARIABLE)
        			res.setExpressionType(ExpressionType.VAR_EXP);
        		String s = AnValue.INT_NAME;
        		switch(insn.getOpcode()) {
        		case Opcodes.LADD:
        			s = AnValue.LONG_NAME;
        			break;
        		case Opcodes.FADD:
        			s = AnValue.FLOAT_NAME;
        			break;
        		case Opcodes.DADD:
        			s = AnValue.DOUBLE_NAME;
        		}
        		res.setClassName(s);
        		return res;
        	} else {
        		res.setExpressionType(ExpressionType.UNDEFINED_EXP);
        		return res;
        	}
        case Opcodes.ISUB:
        case Opcodes.IMUL:
        case Opcodes.IDIV:
        case Opcodes.IREM:
        case Opcodes.ISHL:
        case Opcodes.ISHR:
        case Opcodes.IUSHR:
        case Opcodes.IAND:
        case Opcodes.IOR:
        case Opcodes.IXOR:
        	unknown.setExpressionType(ExpressionType.UNDEFINED_EXP);
    		return unknown;
        case Opcodes.FMUL:
        case Opcodes.FSUB:
        case Opcodes.FDIV:
        case Opcodes.FREM:
        	unknown.setClassName(AnValue.FLOAT_NAME);
    		unknown.setExpressionType(ExpressionType.UNDEFINED_EXP);
    		return unknown;
        case Opcodes.DSUB:
        case Opcodes.DREM:
        case Opcodes.DMUL:
        case Opcodes.DDIV:
        	unknown.setClassName(AnValue.DOUBLE_NAME);
    		unknown.setExpressionType(ExpressionType.UNDEFINED_EXP);
    		return unknown;
        case Opcodes.LDIV:
        case Opcodes.LSUB:
        case Opcodes.LMUL:
        case Opcodes.LREM:
        case Opcodes.LOR:
        case Opcodes.LUSHR:
        case Opcodes.LSHL:
        case Opcodes.LSHR:
        case Opcodes.LAND:
        case Opcodes.LXOR:
        	unknown.setClassName(AnValue.LONG_NAME);
    		unknown.setExpressionType(ExpressionType.UNDEFINED_EXP);
    		return unknown;
        case Opcodes.LCMP:
        case Opcodes.FCMPL:
        case Opcodes.FCMPG:
    	case Opcodes.DCMPL:
    	case Opcodes.DCMPG:
        	/* FIXME to achieve better results, since we will redefine jump behavior in Frame
        	 * or other places, we will define these instructions as follows:
        	 * 
        	 * B(a,b) = [a == b] B(0) +
        	 *			[a < b] B(1) +
        	 *			[a > b] B(-1)
        	 *
        	 * result value will be set by Frame.
        	 */
        	res.setClassName(AnValue.INT_NAME);
        	return res;
    	
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
        	AnValue res = new AnValue(Type.getReturnType(((MethodInsnNode) insn).desc));
        	res.setExpressionType(ExpressionType.UNKNOWN);
        	return res;
        	
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
		AnValue r = v.clone();
		r.setExpressionType(AnValue.leastUpperBound(v.getExpType(),	w.getExpType()));
		return r;
	}

}
