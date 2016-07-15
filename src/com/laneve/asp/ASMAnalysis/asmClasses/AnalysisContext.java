package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;

public class AnalysisContext {

	protected Map<Long, Boolean> threadsStatus;
	protected long threadID;
	
	
	public AnalysisContext() {
		threadsStatus = new HashMap<Long, Boolean>();
		threadID = 0;
	}
	
	public ThreadValue generateThread() {
		ThreadValue t = new ThreadValue(new AnValue(Type.getObjectType(ThreadValue.fullyQualifiedName)),
				threadID);
		threadID++;
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
}
