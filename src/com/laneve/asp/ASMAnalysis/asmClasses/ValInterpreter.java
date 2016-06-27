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
import com.laneve.asp.ASMAnalysis.asmTypes.AnValue.ExpressionType;

/*
 * TODO this class is useless if we don't subclass also Frame. Frame's fields are private, 
 * but getters and setters are provided. Huzzah, problem solved!
 * 
 * FIXME, this doesn't keep track of fields. AnValue and relevant operation must be updated.
 *
 */


public class ValInterpreter extends Interpreter<AnValue> implements Opcodes {

	protected ValInterpreter(int api) {
		super(api);
	}

	@Override
	public AnValue newValue(Type type) {
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
        	v = newValue(Type.INT_TYPE);
        	v.setExpressionValue(AnValue.getConstValue(insn.getOpcode()), ExpressionType.CONST);
            return v;
        case LCONST_0:
        case LCONST_1:
        	v = newValue(Type.LONG_TYPE);
        	v.setExpressionValue(AnValue.getConstValue(insn.getOpcode()), ExpressionType.CONST);
            return v;
        case FCONST_0:
        case FCONST_1:
        case FCONST_2:
        	v = newValue(Type.FLOAT_TYPE);
        	v.setExpressionValue(AnValue.getConstValue(insn.getOpcode()), ExpressionType.CONST);
            return v;
        case DCONST_0:
        case DCONST_1:
        	v = newValue(Type.DOUBLE_TYPE);
        	v.setExpressionValue(AnValue.getConstValue(insn.getOpcode()), ExpressionType.CONST);
            return v;
        case BIPUSH:
        case SIPUSH:
        	v = newValue(Type.INT_TYPE);
        	v.setExpressionValue("ic" + v.getID(), ExpressionType.CONST);
            return v;
        case LDC:
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Integer) {
            	v = newValue(Type.INT_TYPE);
            	v.setExpressionValue(((Integer) cst).toString(), ExpressionType.CONST);
                return v;
            } else if (cst instanceof Float) {
            	v = newValue(Type.FLOAT_TYPE);
            	v.setExpressionValue(((Float) cst).toString(), ExpressionType.CONST);
                return v;
            } else if (cst instanceof Long) {
            	v = newValue(Type.LONG_TYPE);
            	v.setExpressionValue(((Long) cst).toString(), ExpressionType.CONST);
                return v;
            } else if (cst instanceof Double) {
            	v = newValue(Type.DOUBLE_TYPE);
            	v.setExpressionValue(((Double) cst).toString(), ExpressionType.CONST);
                return v;
            } else if (cst instanceof String) {
            	// FIXME do we need strings?
                return newValue(Type.getObjectType("java/lang/String"));
            } else if (cst instanceof Type) {
                int sort = ((Type) cst).getSort();
                if (sort == Type.OBJECT || sort == Type.ARRAY) {
                    return newValue(Type.getObjectType("java/lang/Class"));
                } else if (sort == Type.METHOD) {
                    return newValue(Type
                            .getObjectType("java/lang/invoke/MethodType"));
                } else {
                    throw new IllegalArgumentException("Illegal LDC constant "
                            + cst);
                }
            } else if (cst instanceof Handle) {
                return newValue(Type
                        .getObjectType("java/lang/invoke/MethodHandle"));
            } else {
                throw new IllegalArgumentException("Illegal LDC constant "
                        + cst);
            }
        case GETSTATIC:
        	// TODO maybe rewrite instance of FieldInsnNode to map actual values of fields?
            return newValue(Type.getType(((FieldInsnNode) insn).desc));
        case NEW:
        	// TODO here new "thread" object may be instantiated. is there something we need to do? 
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
		case DNEG:
		case FNEG:
			if (value.getExpType() != ExpressionType.UNDEFINED_EXP
					&& value.getExpType() != ExpressionType.UNKNOWN) {
				v.setInternalValue("-(" + value.getValue() + ")");
				return v;
			} else return value;
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
        case Opcodes.IRETURN:
        case Opcodes.LRETURN:
        case Opcodes.FRETURN:
        case Opcodes.DRETURN:
        case Opcodes.ARETURN:
        	// TODO behaviour types should be "0"
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
        	/* FIXME to achieve better results, since we will redefine jump behavior in Frame\
        	 * or other places, we could define these instructions as follows:
        	 * 
        	 * B(a,b) = [a == b] B(0) +
        	 *			[a < b] B(1) +
        	 *			[a > b] B(-1)
        	 */
        	res.setClassName(AnValue.LONG_NAME);
        	if (value1.getExpType() == ExpressionType.CONST
        			&& value2.getExpType() == ExpressionType.CONST) {
        		long i1 = Long.parseLong(value1.getValue()),
        				i2 = Long.parseLong(value2.getValue());
        		res.setExpressionValue((i1 > i2 ? "1" :
        				(i1 == i2 ? "0" : "-1")),
        				ExpressionType.CONST);
        		return res;
        	/*} else if (value1.getExpType() == ExpressionType.CONST_EXP
        			&& value2.getExpType() == ExpressionType.CONST_EXP) {
        		
        		// TODO parse expression?
        		res.setExpressionValue("dummy",
        				ExpressionType.CONST);
        		return res;*/
        	} else {
        		unknown.setClassName(AnValue.LONG_NAME);
        		return unknown;
        	}
        case Opcodes.FCMPL:
        	res.setClassName(AnValue.INT_NAME);
        	if (value1.getExpType() == ExpressionType.CONST
        			&& value2.getExpType() == ExpressionType.CONST) {
        		Float i1 = Float.parseFloat(value1.getValue()),
        				i2 = Float.parseFloat(value2.getValue());
        		res.setExpressionValue((i1 < i2 ? "1" :
        				(i1 == i2 ? "0" : "-1")),
        				ExpressionType.CONST);
        		return res;
        	} else {
        		unknown.setClassName(AnValue.INT_NAME);
        		return unknown;
        	}
    	case Opcodes.FCMPG:
    		res.setClassName(AnValue.INT_NAME);
        	if (value1.getExpType() == ExpressionType.CONST
        			&& value2.getExpType() == ExpressionType.CONST) {
        		Float i1 = Float.parseFloat(value1.getValue()),
        				i2 = Float.parseFloat(value2.getValue());
        		res.setExpressionValue((i1 > i2 ? "1" :
        				(i1 == i2 ? "0" : "-1")),
        				ExpressionType.CONST);
        		return res;
        	} else {
        		unknown.setClassName(AnValue.INT_NAME);
        		return unknown;
        	}
    	case Opcodes.DCMPL:
    		res.setClassName(AnValue.INT_NAME);
        	if (value1.getExpType() == ExpressionType.CONST
        			&& value2.getExpType() == ExpressionType.CONST) {
        		double i1 = Double.parseDouble(value1.getValue()),
        				i2 = Double.parseDouble(value2.getValue());
        		res.setExpressionValue((i1 < i2 ? "1" :
        				(i1 == i2 ? "0" : "-1")),
        				ExpressionType.CONST);
        		return res;
        	} else {
        		unknown.setClassName(AnValue.INT_NAME);
        		return unknown;
        	}
    	case Opcodes.DCMPG:
    		res.setClassName(AnValue.INT_NAME);
        	if (value1.getExpType() == ExpressionType.CONST
        			&& value2.getExpType() == ExpressionType.CONST) {
        		double i1 = Double.parseDouble(value1.getValue()),
        				i2 = Double.parseDouble(value2.getValue());
        		res.setExpressionValue((i1 > i2 ? "1" :
        				(i1 == i2 ? "0" : "-1")),
        				ExpressionType.CONST);
        		return res;
        	} else {
        		unknown.setClassName(AnValue.INT_NAME);
        		return unknown;
        	}
    	
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
