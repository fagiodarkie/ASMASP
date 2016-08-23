package com.laneve.asp.ASMAnalysis.asmTypes;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

public class AnValue implements Value {
	
	protected static long maxID = 0;
	
	protected static long generateID() {
		return maxID++;
	}
	
	public static String LONG_NAME = "LONG", SHORT_NAME = "SHORT",
			INT_NAME = "INT", FLOAT_NAME = "FLOAT", DOUBLE_NAME = "DOUBLE",	
		BOOL_NAME = "BOOLEAN", CHAR_NAME = "CHAR", STRING_NAME = "STRING",
			REF_NAME = "REFERENCE", THREAD_NAME = "THREAD";
	
	public static String getClassName(Type t) {
		if (t == null)
			return "UNKNOWN";
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
		case Type.OBJECT: {
			if (t.getInternalName().endsWith(".Thread"))
				return THREAD_NAME;
			return t.getInternalName();
		}
		default: return "UNKNOWN";
		}
	}
	
	public static boolean typable(Type t) {
		switch (t.getSort()) {
		case Type.INT:
		case Type.LONG:
			return true;
		case Type.OBJECT:
			if (getClassName(t) == THREAD_NAME)
				return true;
		default: return false;
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
	
	public static boolean isThread(Type t) {
		if (t == null) return false;
//		System.out.println(t.getClassName());
		return (t.getClassName().equalsIgnoreCase("java.lang.Thread"));
	}

	public static boolean compatible(Type t1, Type t2) {
		return getMajorSort(t1) == getMajorSort(t2);
	}

	public static Long getConstValue(int opcode) {
		switch(opcode) {
		case Opcodes.ICONST_0:
		case Opcodes.LCONST_0:
			return new Long(0);
			
		case Opcodes.ICONST_1:
		case Opcodes.LCONST_1:
			return new Long(1);
			
		case Opcodes.ICONST_2:
			return new Long(2);
		
		case Opcodes.ICONST_3:
			return new Long(3);
		
		case Opcodes.ICONST_4:
			return new Long(4);
		
		case Opcodes.ICONST_5:
			return new Long(5);

		case Opcodes.ICONST_M1:
			return new Long(-1);

		default:
			return null;
		}
	}
	
	
	
	public boolean equalValue(AnValue other) {
		return other.type == type && other.className.equalsIgnoreCase(className);
	}
	
	protected Type type;
	protected String className;
	protected String name;
	protected long ID;
	protected Map<String, AnValue> field;
	
	
	
	public AnValue(Type t) {
		type = t;
		className = getClassName(t);
		name = "?";
		field = new HashMap<String, AnValue>();
		ID = generateID();
	}
	
	public AnValue(AnValue a) {
		type = a.type;
		className = a.className;
		name = a.name;
		field = new HashMap<String, AnValue>();
		ID = a.ID;
	}
			
	public AnValue(Type ctype, String string) {
		this(ctype);
		name = string;
	}
	
	public void setField(String name, AnValue val) throws Error {
		if (val.getFieldSize() < 0)
			field.put(name, val);
		else
			throw new Error("Unable to type annidated field objects");
	}
	
	public AnValue getField(String name) {
		return field.get(name);
	}
	
	public int getFieldSize() {
		return field.size();
	}

	public String getClassName() {
		return className;
	}

	public AnValue clone() {
		return new AnValue(this);
	}
	
	public String getName() {
		return name;
	}
	
	public void setClassName(String name) {
		className = name;
	}

	public long getID() {
		return ID;
	}
	
	@Override
	public int getSize() {
		return type.getSize();
	}

	public String toString() {
		String[] r = className.split("/");
		return r[r.length - 1];
	}
	
	public Type getType() {
		return type;
	}
	
}
