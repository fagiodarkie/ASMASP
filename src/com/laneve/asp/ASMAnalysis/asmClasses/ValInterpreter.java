package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.AnValue.ExpressionType;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnValue ternaryOperation(AbstractInsnNode insn, AnValue value1,
			AnValue value2, AnValue value3) throws AnalyzerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnValue naryOperation(AbstractInsnNode insn,
			List<? extends AnValue> values) throws AnalyzerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnOperation(AbstractInsnNode insn, AnValue value,
			AnValue expected) throws AnalyzerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AnValue merge(AnValue v, AnValue w) {
		// TODO Auto-generated method stub
		return null;
	}

}
