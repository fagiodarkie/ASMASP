package com.laneve.asp.ASMAnalysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;
import com.laneve.asp.ASMAnalysis.utils.Streamifier;

public class ASMAnalysis {

	protected static AnalysisContext context;
	/**
	 * @param args: java ASMAnalysis [directory]
	 */
	public static void main(String[] args) {
		/* TODO
		 * - find and load class files;
		 * - input the class files for analysis
		 * ?- sort them in order to have other methods analyzed before they are needed?
		 * - invoke analyzer methods;
		 * - get frames;
		 * |-- ! it may be the case that each frame only gives one "Value". If so, cycles would not be "repeated" n times
		 * 		unless we generate the behavioural type while analysis goes on. Maybe it's better to have a 
		 * 		parameterized Value that reflect the types we defined (B_i(Frame, Stack) ::= type | type;B_j(F',S') | B_j(F',S') | [cond]B + [cond]B 
		 * - print frames result. 
		 */
		
		/*
		 * start by taking the 1st argument as the folder in which the classfiles are located.
		 */
		context = new AnalysisContext();
		List<InputStream> streams = Streamifier.streamifyDirectory(args[2]);
		
		for (int i = 0; i < streams.size(); ++i) {
			try {
				ClassReader r = new ClassReader(streams.get(i));
				ClassNode n = new ClassNode();
				r.accept(n, 0);
				for (MethodNode m: n.methods) {
					context.createMethodNode(m.name, m);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
	}

}
