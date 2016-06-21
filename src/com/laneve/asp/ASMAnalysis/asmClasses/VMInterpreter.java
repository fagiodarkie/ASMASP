package com.laneve.asp.ASMAnalysis.asmClasses;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import com.laneve.asp.ASMAnalysis.types.allocator.Allocation;


public class VMInterpreter extends Interpreter<Allocation> {

	protected String allocationSignature, deallocationSignature;
	

	protected VMInterpreter(int api) {
		super(api);
	}
	
	public void setAllocationSignature(String signature) {
		allocationSignature = signature;
	}
	public void setDeallocationSignature(String signature) {
		deallocationSignature = signature;
	}

	@Override
	public Allocation newValue(Type type) {
		// FIXME no way to use Type efficacely
		return null;
	}

	@Override
	public Allocation newOperation(AbstractInsnNode insn)
			throws AnalyzerException {
        switch (insn.getOpcode()) {
        case Opcodes.INVOKEDYNAMIC:
        case Opcodes.INVOKEINTERFACE:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKEVIRTUAL:
        	Allocation r = new Allocation();
        	if (((MethodInsnNode) insn).name.equalsIgnoreCase(allocationSignature))
        		r.acquire();
        	else if (((MethodInsnNode) insn).name.equalsIgnoreCase(deallocationSignature))
        		r.release();
        	else return null;
        	return r;
        default:
        	return null;
        }
	}

	@Override
	public Allocation copyOperation(AbstractInsnNode insn, Allocation value)
			throws AnalyzerException {
		// TODO controllare se questo metodo è effettivamente invocato quando viene "copiato" un Frame:
		// in tal caso, si possono implementare metodi di copia.
		return value;
	}

	@Override
	public Allocation unaryOperation(AbstractInsnNode insn, Allocation value)
			throws AnalyzerException {
		throw new AnalyzerException(insn, "Allocation values do not support operators.");
	}

	@Override
	public Allocation binaryOperation(AbstractInsnNode insn, Allocation value1,
			Allocation value2) throws AnalyzerException {
		throw new AnalyzerException(insn, "Allocation values do not support operators.");
	}

	@Override
	public Allocation ternaryOperation(AbstractInsnNode insn,
			Allocation value1, Allocation value2, Allocation value3)
			throws AnalyzerException {
		throw new AnalyzerException(insn, "Allocation values do not support operators.");
	}

	@Override
	public Allocation naryOperation(AbstractInsnNode insn,
			List<? extends Allocation> values) throws AnalyzerException {
		throw new AnalyzerException(insn, "Allocation values do not support operators.");
	}

	@Override
	public void returnOperation(AbstractInsnNode insn, Allocation value,
			Allocation expected) throws AnalyzerException {
	}

	@Override
	public Allocation merge(Allocation v, Allocation w) {
		if(v.getStatus() != w.getStatus()) {
			return null;
		}
		return v;
	}

}
