package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;

public class ValInterpreter extends Interpreter<AnValue>{

	protected ValInterpreter(int api) {
		super(api);
	}

	@Override
	public AnValue newValue(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
		// TODO Auto-generated method stub
		return null;
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
