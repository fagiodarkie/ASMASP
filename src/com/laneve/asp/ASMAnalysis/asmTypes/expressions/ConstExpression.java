package com.laneve.asp.ASMAnalysis.asmTypes.expressions;
import org.objectweb.asm.Type;

public class ConstExpression extends IExpression {
	
protected Long constExp;
	
	public ConstExpression(Type t, Long Const) {
		super(t);
		constExp = Const;
	}
	
	@Override
	public Long evaluate() {
		return constExp;
		
	}

	@Override
	public IExpression clone() {
		return new ConstExpression(type, constExp);
	}

	@Override
	public boolean equalExpression(IExpression iExpression) {
		if (!(iExpression instanceof ConstExpression)) return false;
		return constExp == ((ConstExpression)iExpression).constExp;
	}

}
