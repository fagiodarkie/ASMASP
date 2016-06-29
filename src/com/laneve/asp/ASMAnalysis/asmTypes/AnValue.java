package com.laneve.asp.ASMAnalysis.asmTypes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

public class AnValue implements Value {
	
	public static String LONG_NAME = "LONG", SHORT_NAME = "SHORT",
			INT_NAME = "INT", FLOAT_NAME = "FLOAT", DOUBLE_NAME = "DOUBLE",
			BOOL_NAME = "BOOLEAN", CHAR_NAME = "CHAR", STRING_NAME = "STRING",
			REF_NAME = "REFERENCE";
	
	public static String getClassName(Type t) {
		switch(t.getSort()) {
		case Type.ARRAY: return t.getInternalName() + "[]";
		case Type.BOOLEAN: return BOOL_NAME;
		case Type.CHAR: return CHAR_NAME;
		case Type.DOUBLE: return DOUBLE_NAME;
		case Type.FLOAT: return FLOAT_NAME;
		case Type.INT: return INT_NAME;
		case Type.LONG: return LONG_NAME;
		case Type.SHORT: return SHORT_NAME;
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

	public static String getConstValue(int opcode) {
		switch(opcode) {
		case Opcodes.ICONST_0:
		case Opcodes.FCONST_0:
		case Opcodes.DCONST_0:
		case Opcodes.LCONST_0:
			return "0";
			
		case Opcodes.ICONST_1:
		case Opcodes.FCONST_1:
		case Opcodes.DCONST_1:
		case Opcodes.LCONST_1:
			return "1";
			
		case Opcodes.ICONST_2:
		case Opcodes.FCONST_2:
			return "2";
		
		case Opcodes.ICONST_3:
			return "3";
		
		case Opcodes.ICONST_4:
			return "4";
		
		case Opcodes.ICONST_5:
			return "5";

		case Opcodes.ICONST_M1:
			return "-1";

		default:
			return "";
		}
	}

	public static ExpressionType leastUpperBound(ExpressionType t1, ExpressionType t2) {
		if (t1 == ExpressionType.UNKNOWN || t2 == ExpressionType.UNKNOWN)
			return ExpressionType.UNKNOWN;
		if (t1 == ExpressionType.UNDEFINED_EXP || t2 == ExpressionType.UNDEFINED_EXP)
			return ExpressionType.UNDEFINED_EXP;
		if (t1 == ExpressionType.VAR_EXP || t2 == ExpressionType.VAR_EXP)
			return ExpressionType.VAR_EXP;
		if (t1 == ExpressionType.VARIABLE || t2 == ExpressionType.VARIABLE)
			return ExpressionType.VARIABLE;
		if (t1 == ExpressionType.CONST_EXP || t2 == ExpressionType.CONST_EXP)
			return ExpressionType.CONST_EXP;
		return ExpressionType.CONST;

		
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
	// protected int ID;
	
	
	public AnValue(Type t) {
		//ID = generateID();
		type = t;
		className = getClassName(t);
	}
	
	public AnValue(AnValue a) {
		type = a.type;
		className = a.className;
		value = a.value;
		exType = a.exType;
		//ID = a.ID;
	}
	
	public String toString() {
		String type =(defined() ?  className : "-");
		return getValue() + ":" + type;
	}
	
	public boolean defined() {
		return (exType != ExpressionType.UNDEFINED_EXP && exType != ExpressionType.UNKNOWN);
	}
	
	public boolean equals(AnValue o) {
		//if (ID != o.ID)
		//	return false;
		if (exType != o.exType)
			return false;
		if (className != o.className)
			return false;
		if (type != o.type)
			return false;
		if (value != o.value)
			return false;
		return true;
	}
	
	public String getClassName() {
		return className;
	}

	public String getValue() {
		return value;
	}
	
//	public int getID() {
//		return ID;
//	}
	
	public ExpressionType getExpType() {
		return exType;
	}
	
	public AnValue clone() {
		return new AnValue(this);
	}
	
	public void setInternalValue(String val) {
		value = val;
	}
	
	public void setClassName(String name) {
		className = name;
	}
	
	public void setExpressionType(ExpressionType t) {
		exType = t;
	}
	
	public void setExpressionValue(String value, ExpressionType expression) {
		this.value = value;
		this.exType = expression;
	}
	
	@Override
	public int getSize() {
		if (type.getSort() == Type.DOUBLE || type.getSort() == Type.LONG)
			return 2;
		return 1;
	}

	


}
