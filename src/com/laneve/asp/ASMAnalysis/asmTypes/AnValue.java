package com.laneve.asp.ASMAnalysis.asmTypes;

import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Value;
import org.objectweb.asm.Type;

public class AnValue implements Value {
	
	public static String getClassName(Type t) {
		switch(t.getSort()) {
		case Type.ARRAY: return t.getInternalName() + "[]";
		case Type.BOOLEAN: return "BOOLEAN";
		case Type.CHAR: return "CHAR";
		case Type.DOUBLE: return "DOUBLE";
		case Type.FLOAT: return "FLOAT";
		case Type.INT: return "INT";
		case Type.LONG: return "LONG";
		case Type.SHORT: return "SHORT";
		case Type.METHOD: return t.getDescriptor();
		case Type.OBJECT: return t.getInternalName();
		default: return "UNKNOWN";
		}
	}
	
	public static int getMajorSort(Type t) {
		switch(t.getSort()) {
		case Type.ARRAY: return 0;
		case Type.BOOLEAN: return 1;
		case Type.DOUBLE:
		case Type.FLOAT: return 2;
		case Type.CHAR:
		case Type.INT:
		case Type.LONG:
		case Type.SHORT: return 3;
		case Type.METHOD: return 4;
		case Type.OBJECT: return 5;
		default: return -1;
		}
	}

	public static boolean compatible(Type t1, Type t2) {
		return getMajorSort(t1) == getMajorSort(t2);
	}

	public enum ExpressionType {CONST, VARIABLE, UNKNOWN, CONST_EXP, VAR_EXP, UNDEFINED_EXP};
	protected static int maxID = 0;
	protected static int generateID() {
		return maxID++;
	}
	
	protected Type type;
	protected String className;
	protected String value;
	protected ExpressionType exType;
	protected int ID;	
	
	
	public AnValue(Type t) {
		ID = generateID();
		type = t;
		className = getClassName(t);
	}

	public AnValue(Type t, IincInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public AnValue(Type t, IntInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public AnValue(Type t, InvokeDynamicInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public AnValue(Type t, JumpInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public AnValue(Type t, MethodInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public AnValue(Type t, VarInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public AnValue(Type t, TypeInsnNode n) {
		ID = generateID();
		type = t;
		className = getClassName(t);
		// TODO
	}

	public String getClassName() {
		return className;
	}

	
	@Override
	public int getSize() {
		if (type.getSort() == Type.DOUBLE || type.getSort() == Type.LONG)
			return 2;
		return 1;
	}


}
