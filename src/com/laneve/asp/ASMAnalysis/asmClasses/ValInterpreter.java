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
            return newValue(Type.INT_TYPE);
        case LCONST_0:
        case LCONST_1:
            return newValue(Type.LONG_TYPE);
        case FCONST_0:
        case FCONST_1:
        case FCONST_2:
            return newValue(Type.FLOAT_TYPE);
        case DCONST_0:
        case DCONST_1:
            return newValue(Type.DOUBLE_TYPE);
        case BIPUSH:
        case SIPUSH:
            return newValue(Type.INT_TYPE);
        case LDC:
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Integer) {
                return newValue(Type.INT_TYPE);
            } else if (cst instanceof Float) {
                return newValue(Type.FLOAT_TYPE);
            } else if (cst instanceof Long) {
                return newValue(Type.LONG_TYPE);
            } else if (cst instanceof Double) {
                return newValue(Type.DOUBLE_TYPE);
            } else if (cst instanceof String) {
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
            return newValue(Type.getType(((FieldInsnNode) insn).desc));
        case NEW:
            return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
        default:
            throw new Error("Internal error.");
        }
	}

	@Override
	public AnValue copyOperation(AbstractInsnNode insn, AnValue value)
			throws AnalyzerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnValue unaryOperation(AbstractInsnNode insn, AnValue value)
			throws AnalyzerException {
		// TODO Auto-generated method stub
		return null;
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
