package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.bTypes.Behaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;

public class AnalysisContext {

	protected Map<Long, Boolean> threadsStatus, analyzeMethods;
	protected Map<Long, IExpression> returnValue;
	protected Map<Long, String> methodID, owner;
	protected Map<Long, List<Long>> depends;
	protected Map<Long, List<BehaviourFrame>> methodFrames;
	protected long threadCounter, methodCounter;
	private Map<Long, MethodNode> methodNodes;
	private Map<Long, Behaviour> methodBehaviour;
	
	
	public AnalysisContext() {
		threadsStatus = new HashMap<Long, Boolean>();
		analyzeMethods = new HashMap<Long, Boolean>();
		returnValue = new HashMap<Long, IExpression>();
		methodID = new HashMap<Long, String>();
		owner = new HashMap<Long, String>();
		depends = new HashMap<Long, List<Long>>();
		methodFrames = new HashMap<Long, List<BehaviourFrame>>();
		threadCounter = methodCounter = 0;
		methodNodes = new HashMap<Long, MethodNode>();
		methodBehaviour = new HashMap<Long, Behaviour>();
	}
	
	public ThreadValue generateThread() {
		ThreadValue t = new ThreadValue(new AnValue(Type.getObjectType(ThreadValue.fullyQualifiedName)),
				threadCounter);
		threadCounter++;
		return t;
	}
	
	public ThreadResource allocateThread(ThreadValue t) {
		if (!threadsStatus.containsKey(t.getID())) {
			threadsStatus.put(t.getID(), true);
			return new ThreadResource(t.getID(), ThreadResource.ACQUIRE);			
		} else {
			return new ThreadResource(t.getID(), ThreadResource.ALREADY_ACQUIRED);
		}
	}

	public ThreadResource deallocateThread(ThreadValue t) {
		if (threadsStatus.get(t.getID())) {
			threadsStatus.put(t.getID(), false);
			return new ThreadResource(t.getID(), ThreadResource.RELEASE);
		} else return new ThreadResource(t.getID(), ThreadResource.ALREADY_RELEASED);
	}

	public void signalDependancy(String methodName, List<String> deps) {
		Long currentMethodID = getKeyOfMethod(methodName);
		for (String s: deps) {
			
			if (!methodID.values().contains(s))
				continue;
			// add the index of methods.
			Long k = getKeyOfMethod(s);
			if (!depends.get(currentMethodID).contains(k)) {
				depends.get(currentMethodID).add(k);
			}
		}
			
	}
	
	public void modified(String method) {
		Long key = getKeyOfMethod(method);
		for (long i = 0; i < methodCounter; ++i) {
			if (depends.get(i).contains(key)) {
				analyzeMethods.put(i, true);
			}
		}
	}
	
	public void setReturnExpression(String method, AnValue value) {
		Long key = getKeyOfMethod(method);
		if (returnValue.get(key) == null)
			returnValue.put(key, (IExpression) value);
		else {
			if (!((IExpression) value).equalExpression(returnValue.get(key))) {
				throw new Error("Analysis error: unable to analyze methods returning different values.");
			}
		}
	}
	
	private Long getKeyOfMethod(String method) {
		if (!methodID.containsValue(method)) {
			throw new Error("Method not found: " + method);
		}
		for (Long i: methodID.keySet()) {
			if (methodID.get(i).equalsIgnoreCase(method))
				return i;
		}
		return null;
	}
	
	private void createMethod(String methodName) {
		// create new methodID
		methodID.put(methodCounter, methodName);
		depends.put(methodCounter, new ArrayList<Long>());
		analyzeMethods.put(methodCounter, true);
		methodCounter++;
	}


	public void createMethodNode(String className, String name, MethodNode method) {
		if (methodID.containsValue(name))
			try {
				throw new Exception("Nome gia esistente: " + name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			createMethod(name);
		long k = getKeyOfMethod(name);
		methodNodes.put(k, method);
		methodBehaviour.put(k, null);
		owner.put(k, className);
	}

	public void analyze(String entryPoint) throws AnalyzerException {
		long k = getKeyOfMethod(entryPoint);
		List<Long> analysisList = new ArrayList<Long>();
		VMAnalyzer analyzer = new VMAnalyzer(new ValInterpreter(), this);
				
		/* deep visit of the dependancies. we assume that:
		 * 1) no recursion is done within method bodies (no f(n) / f(n - 1);
		 * 2) no circular definitions (no f = g and g = f);
		 * 
		 * Once deep visit ends (it must end) we type following the queue.
		 */
		
		analysisList.add(k);
		// reanalyze methods until a fixed point is reached
		for (Long currentMethodID: analysisList) {
			// if the method is already at fixed point don't touch it
			if (!analyzeMethods.get(currentMethodID))
				continue;
			
			// else, analyze it and put all its dependancies to be analyzed too.
			// also all methods which depends on it, if behaviour changes.
			// as side effect, the return value is automatically updated.
			BehaviourFrame[] frames = analyzer.analyze(owner.get(currentMethodID), methodNodes.get(currentMethodID));
			for (Long j: depends.get(currentMethodID)) {
				// we put on all its dependancies
				analysisList.add(j);
			}

			// Initialize entrypoint behaviour. As side effect, analyzer updates the dependancies of the method.
			methodFrames.put(currentMethodID, Arrays.asList(frames));
			
			// if the new behaviour is different from the past one, also update all methods depending on this one.
			Behaviour old = methodBehaviour.get(currentMethodID);
			Behaviour updatedBehaviour = computeBehaviour(frames);
			
			if (!old.equalBehaviour(updatedBehaviour)) {
				methodBehaviour.put(currentMethodID, updatedBehaviour);
				for (long i = 0; ((i < methodCounter) && (i != currentMethodID)); ++i) {
					if (depends.get(i).contains(currentMethodID) && !analysisList.contains(i)) {
						analysisList.add(i);
						analyzeMethods.put(i, true);					
					}
				}
			}
			
			// finally, notify that we checked the method.
			analyzeMethods.put(currentMethodID, false);
			
		}
		
		
	}

	protected Behaviour computeBehaviour(BehaviourFrame[] frames) {
		// TODO Compute behaviour (LAM?) of frame.
		return null;
	}

}
