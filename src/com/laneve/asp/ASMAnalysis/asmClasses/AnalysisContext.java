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
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.ConstExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.bTypes.Atom;
import com.laneve.asp.ASMAnalysis.bTypes.ConcatBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.MethodBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;

public class AnalysisContext {

	protected Map<Long, Integer> threadsStatus;
	protected Map<Long, Boolean> analyzeMethods, modifiedReturnExpression;
	protected Map<Long, IExpression> returnValue;
	protected Map<Long, String> methodID, owner;
	protected Map<Long, List<Long>> depends;
	protected Map<Long, List<BehaviourFrame>> methodFrames;
	protected long threadCounter, methodCounter;
	private Map<Long, MethodNode> methodNodes;
	private Map<Long, IBehaviour> methodBehaviour;
	private String resourceClass, allocationCall, deallocationCall;
	
	
	public AnalysisContext() {
		threadsStatus = new HashMap<Long, Integer>();
		analyzeMethods = new HashMap<Long, Boolean>();
		modifiedReturnExpression = new HashMap<Long, Boolean>();
		returnValue = new HashMap<Long, IExpression>();
		methodID = new HashMap<Long, String>();
		owner = new HashMap<Long, String>();
		depends = new HashMap<Long, List<Long>>();
		methodFrames = new HashMap<Long, List<BehaviourFrame>>();
		threadCounter = methodCounter = 0;
		methodNodes = new HashMap<Long, MethodNode>();
		methodBehaviour = new HashMap<Long, IBehaviour>();
		
		resourceClass = "java/lang/Thread";
		allocationCall = resourceClass + ".run()V";
		deallocationCall = resourceClass + ".join()V";
	}
	
	public void setAllocationVariables(String className, String alloc, String dealloc) {
		resourceClass = className;
		allocationCall = alloc;
		deallocationCall = dealloc;
	}
	
	public ThreadValue generateThread() {
		ThreadValue t = new ThreadValue(new AnValue(Type.getObjectType(ThreadValue.fullyQualifiedName)),
				threadCounter, this);
		threadsStatus.put(threadCounter, ThreadResource.ALLOCATED);
		threadCounter++;
		return t;
	}
	
	public ThreadResource allocateThread(ThreadValue t) {
		if (threadsStatus.get(t.getID()) == ThreadResource.ALLOCATED) {
			threadsStatus.put(t.getID(), ThreadResource.ACQUIRE);
			return new ThreadResource(t.getID(), ThreadResource.ACQUIRE);			
		} else {
			return new ThreadResource(t.getID(), ThreadResource.ALREADY_ACQUIRED);
		}
	}

	public ThreadResource deallocateThread(ThreadValue t) {
		if (threadsStatus.get(t.getID()) == ThreadResource.ACQUIRE) {
			threadsStatus.put(t.getID(), ThreadResource.RELEASE);
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

		if (!returnValue.get(key).equalValue((IExpression)value)) {
			returnValue.put(key, (IExpression) value);
			modifiedReturnExpression.put(key, true);
			//System.out.println("Method " + method + " was modified: new value is " + value.toString());
		}
		
		
		/*
		 * TODO check on equal return expression will be done in a later stage, when we are able
		 * to understand when we are trying to set 2 different return expression in the same analysis cycle.
		 * 
		 * if (returnValue.get(key) == null)
		else {
			if (!((IExpression) value).equalExpression(returnValue.get(key))) {
				throw new Error("Analysis error: unable to analyze methods returning different values.");
			}
		}*/
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
	
	public IExpression getReturnValueOfMethod(String methodName) {
		// All foreign methods are treated as null. actual usage of this value will result in cast errors.
		long key = getKeyOfMethod(methodName);
		return returnValue.get(key);
	}

	public void createMethodNode(String className, String name, MethodNode method) {
		methodNodes.put(methodCounter, method);
		methodBehaviour.put(methodCounter, new Atom(Atom.RETURN));
		owner.put(methodCounter, className);
		methodID.put(methodCounter, name);
		depends.put(methodCounter, new ArrayList<Long>());
		analyzeMethods.put(methodCounter, true);
		returnValue.put(methodCounter, new ConstExpression(Type.INT_TYPE, new Long(0)));
		modifiedReturnExpression.put(methodCounter, false);
		methodCounter++;
	}

	public void analyze(String entryPoint) throws AnalyzerException {
		long k = getKeyOfMethod(entryPoint);
		List<Long> analysisList = new ArrayList<Long>();
		VMAnalyzer analyzer = new VMAnalyzer(new ValInterpreter(this), this);
		
		analysisList.add(k);
		// reanalyze methods until a fixed point is reached
		for (int i = 0; i < analysisList.size(); ++i) {
			Long currentMethodID = analysisList.get(i);
			
			// if the method is already at fixed point don't touch it
			if (!analyzeMethods.get(currentMethodID))
				continue;

			System.out.println("Analyzing method " + methodID.get(currentMethodID));
			
			// else, analyze it and put all its dependancies to be analyzed too.
			// also all methods which depends on it, if behaviour changes.
			// as side effect, the return value is automatically updated.
			BehaviourFrame[] frames = analyzer.analyze(owner.get(currentMethodID), methodNodes.get(currentMethodID));

			for (Long j: depends.get(currentMethodID)) {
				// we put on all its dependancies
				analysisList.add(j);
			}
			
			// if the return value was updated, also examine all methods depending on this.
			if (modifiedReturnExpression.get(currentMethodID)) {
				//System.out.println("Since the method return value was modified, we also reanalyze:");
				for (long j = 0; j < methodCounter; ++j) {
					if (depends.get(j).contains(currentMethodID)) {
						analysisList.add(j);
						analyzeMethods.put(j, true);
						//System.out.println(methodID.get(j));
					}
				}
				modifiedReturnExpression.put(currentMethodID, false);
			}
			
			methodFrames.put(currentMethodID, Arrays.asList(frames));
			
			// if the new behaviour is different from the past one, also update all methods depending on this one.
			IBehaviour old = methodBehaviour.get(currentMethodID);
			IBehaviour updatedBehaviour = computeBehaviour(frames);
			
			if (!old.equalBehaviour(updatedBehaviour)) {
				methodBehaviour.put(currentMethodID, updatedBehaviour);
				for (long j = 0; ((j < methodCounter) && (j != currentMethodID)); ++j) {
					if (depends.get(j).contains(currentMethodID) && !analysisList.contains(j)) {
						analysisList.add(j);
						analyzeMethods.put(j, true);					
					}
				}
			}
			
		// finally, notify that we checked the method.
			analyzeMethods.put(currentMethodID, false);
			
		}
		
		for (long i = 0; i < methodCounter; ++i) {
			System.out.println("Method " + methodID.get(i) + " has behaviour " + methodBehaviour.get(i));
			System.out.println("Method " + methodID.get(i) + " has return value " + returnValue.get(i));
		}
		
	}

	protected IBehaviour computeBehaviour(BehaviourFrame[] frames) {
		// TODO Compute behaviour (LAM?) of frame.
		
		List<IBehaviour> l = new ArrayList<IBehaviour>();
		for (int i = 0; i < frames.length; ++i) {
			if (frames[i] != null) {
				IBehaviour b = frames[i].getBehaviour();
				if (b != null)
					l.add(b);
			}
		}
		
		if (l.size() == 0)
			return new Atom(Atom.RETURN);
		
		IBehaviour res = l.get(0);
		for (int i = 1; i < l.size(); ++i) {
			IBehaviour left = res.clone();
			IBehaviour right = l.get(i);
			res = new ConcatBehaviour(left, right);
			
		}		
		
		//System.out.println(res.toString());
		
		return res;
	}

	public boolean hasBehaviour(String currentMethodName) {
		
		return methodID.containsValue(currentMethodName);
	}
	
	public boolean isAtomicBehaviour(String currentMethodName) {
		return currentMethodName.equalsIgnoreCase(allocationCall)
				|| currentMethodName.equalsIgnoreCase(deallocationCall);
	}

	public ThreadResource createAtom(AnValue anValue, String currentMethodName) {
		if (isAtomicBehaviour(currentMethodName)) {
			if (currentMethodName.equalsIgnoreCase(allocationCall))
				return allocateThread((ThreadValue)anValue);
			else return deallocateThread((ThreadValue)anValue);
		}
		return null;
	}

	public IBehaviour getBehaviour(String currentMethodName, List<? extends AnValue> values) {
		return new MethodBehaviour(currentMethodName, values);
		//return methodBehaviour.get(getKeyOfMethod(currentMethodName));
	}


	public Integer getStatusOfThread(long id) {
		return threadsStatus.get(id);
	}

}
