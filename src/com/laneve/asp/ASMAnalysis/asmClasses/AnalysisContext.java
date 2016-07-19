package com.laneve.asp.ASMAnalysis.asmClasses;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;

public class AnalysisContext {

	protected Map<Long, Boolean> threadsStatus, analyzeMethods;
	protected Map<Long, AnValue> returnValue;
	protected Map<Long, String> methodID;
	protected Map<Long, List<Long>> depends;
	protected List<String> analyzeClasses;
	protected long threadCounter, methodCounter;
	
	
	public AnalysisContext() {
		threadsStatus = new HashMap<Long, Boolean>();
		analyzeMethods = new HashMap<Long, Boolean>();
		returnValue = new HashMap<Long, AnValue>();
		methodID = new HashMap<Long, String>();
		analyzeClasses = new ArrayList<String>();
		depends = new HashMap<Long, List<Long>>();
		threadCounter = methodCounter = 0;
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

	public void setClassFiles(List<InputStream> streamifyDirectory) {
		// TODO?
	}

	public void signalDependancy(String methodName, List<String> deps) {
		Long currentMethodID = getKeyOfMethod(methodName);
		for (String s: deps) {
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
		returnValue.put(key, value);
	}
	
	private Long getKeyOfMethod(String method) {
		if (!methodID.containsValue(method)) {
			createMethod(method);
			return methodCounter;
		}
		for (Long i: methodID.keySet()) {
			if (methodID.get(i) == method)
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
}
