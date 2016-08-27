package com.laneve.asp.ASMAnalysis.asmTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

public class AnValue implements Value {
	
	protected static long maxID = 0;
	protected static int maxDepth = 1;
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
		if (!other.className.equalsIgnoreCase(className))
			return false;
		
		for (String f: field.keySet())
			if (!other.getField(f).equalValue(getField(f)))
				return false;
		
		return true;
	}
	
	protected Type type;
	protected String className;
	protected boolean isVariable, updated;
	protected String name, fieldName;
	protected long ID;
	protected Map<String, AnValue> field;
	
	
	
	public AnValue(Type t) {
		type = t;
		className = getClassName(t);
		name = "?";
		field = new HashMap<String, AnValue>();
		ID = generateID();
		isVariable = false;
		updated = false;
	}
	
	public AnValue(AnValue a) {
		type = a.type;
		className = a.className;
		name = a.name;
		fieldName = a.fieldName;
		field = new HashMap<String, AnValue>();
		
		for (Entry<String, AnValue> x : a.field.entrySet()) {
			field.put(x.getKey(), x.getValue().clone());
		}
		
		ID = a.ID;
		isVariable = a.isVariable;
		updated = a.updated;
	}
			
	public AnValue(Type ctype, String string) {
		this(ctype);
		name = string;
		fieldName = string;
	}
	
	public List<String> getFieldNames() {
		List<String> a = new ArrayList<String>(field.keySet());
		java.util.Collections.sort(a);
		return a;
	}
	
	public void setFieldName(String n) {
		fieldName = n;
		for (AnValue a: field.values())
			a.iAmYourFather(fieldName);
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setField(String n, AnValue val) throws Error {
		if (val.getDepth() < maxDepth) {
//			if (getField(n) != null)
//				val.ID = getField(n).ID;
			val.setFieldName(n);
			val.iAmYourFather(fieldName);
			val.updated = true;
			field.put(n, val);
			updated = true;
		}
		else
			throw new Error("Object " + name + " unable to annidate field objects of depth > " + maxDepth);
	}
	
	public int getDepth() {
		if (field.size() == 0) return 0;
		int d = 0;
		for (AnValue a: field.values()) {
			if (a.getDepth() > d)
				d = a.getDepth();
		}
		return d + 1;
	}
	
	public boolean isVariable() {
		return isVariable;
	}
	
	public void setVariable(boolean b) {
		isVariable = b;
		for (AnValue a : field.values())
			a.setVariable(b);
	}
		
	public AnValue getField(String n) {
		if (!n.contains("\\."))
			return field.get(n);
		int dot = n.indexOf("\\.");
		String o1 = n.substring(0, dot - 1), f = n.substring(dot + 1);
		return field.get(o1).getField(f);
	}
	
	public void iAmYourFather(String fatherName) {
		if (!fieldName.contains("."))
			fieldName = fatherName  + "." + fieldName;
		else {
			fieldName = fatherName + "." + fieldName.substring(fieldName.lastIndexOf('.') + 1);
		}
		for (AnValue a: field.values())
			a.iAmYourFather(fieldName);
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
	
	public void setClassName(String n) {
		className = n;
	}

	public long getID() {
		return ID;
	}
	
	@Override
	public int getSize() {
		return type.getSize();
	}

	public String toString() {
		String[] r = name.split("\\.");
		return r[r.length - 1];
	}
	
	public Type getType() {
		return type;
	}

	public void setUpdated(boolean b) {
		updated = b;
		for (AnValue f: field.values())
			f.setUpdated(b);
	}
	
	public List<AnValue> getFields() {
		// updated to ArrayList to provide unique field ordering.
		List<AnValue> v = new ArrayList<AnValue>();
		for (String s: getFieldNames())
			v.add(getField(s));
		return v;
	}

	public boolean updated() {
		return updated;
	}

	public void updateByID(long id, AnValue newValue) {
		for (Entry<String, AnValue> e : field.entrySet())
			if (e.getValue().getID() == id)
				field.put(e.getKey(), newValue);
			else e.getValue().updateByID(id, newValue);
	}

	
}
