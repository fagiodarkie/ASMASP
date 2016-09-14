package com.laneve.asp.ASMAnalysis;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.laneve.asp.ASMAnalysis.asmClasses.AnalysisContext;
import com.laneve.asp.ASMAnalysis.utils.Streamifier;

public class ASMAnalysis {

	protected static AnalysisContext context;
	/**
	 * @param args: java ASMAnalysis [directory] [entryPoint method]
	 * @throws AnalyzerException 
	 */
	public static void main(String[] args) throws AnalyzerException {
		
		/*
		 * start by taking the 1st argument as the folder in which the classfiles are located.
		 */
		context = new AnalysisContext();

		Path p = Paths.get(System.getProperty("user.home"), "git", "ASMASP", "bin", "com", "laneve", "asp", "ASMAnalysis", "tests");
		Path directory = (args.length > 2 ? Paths.get(args[2]) : p);
		String entryPoint = (args.length > 3 ? args[3] : "com/laneve/asp/ASMAnalysis/tests/Tests.main()V");
		
		List<InputStream> streams = Streamifier.streamifyDirectory(directory);
		
		for (int i = 0; i < streams.size(); ++i) {
			try {
				ClassReader r = new ClassReader(streams.get(i));
				ClassNode n = new ClassNode();
				r.accept(n, 0);
//				System.out.println("Found class " + n.name);
				List<String> a = new ArrayList<String>();
				Map<String, Type> fs = new HashMap<String, Type>();
				for (FieldNode m: n.fields) {
					a.add(m.name);
					fs.put(m.name, Type.getType(m.desc));
	//				System.out.println("With field: " + m.name + ":" + m.desc);
				}
				java.util.Collections.sort(a);
				for (String fname: a)
					context.signalField(r.getClassName(), fname, fs.get(fname));

				for (MethodNode m: n.methods) {
					context.createMethodNode(r.getClassName(), n.name + "." + m.name + m.desc, m);
	//				System.out.println("With method: " + m.name + m.desc);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		context.analyze(entryPoint);
	}

}
