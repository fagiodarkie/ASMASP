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
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.FunctionCallExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.bTypes.Atom;
import com.laneve.asp.ASMAnalysis.bTypes.ConcatBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ConditionalJump;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.MethodBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;
import com.laneve.asp.ASMAnalysis.utils.Names;

public class AnalysisContext {

	protected Map<Long, Integer> threadsStatus;
	protected Map<Long, Boolean> analyzeMethods, modifiedReturnExpression, dynamicMethod;
	protected Map<Long, IExpression> returnValue;
	protected Map<Long, String> methodID, owner;
	protected Map<Long, List<Long>> depends;
	protected Map<Long, Map<String, String>> releasedParameters;
	protected Map<Long, List<String>> paramString;
	protected Map<Long, List<BehaviourFrame>> methodFrames;
	protected long threadCounter, methodCounter;
	protected Map<Long, MethodNode> methodNodes;
	protected Map<Long, Map<String, IBehaviour>> methodBehaviour;
	protected String resourceClass, allocationCall, deallocationCall;
	protected Map<Character, Integer> threadVariableStatus;
	protected Map<String, List<String>> objectFields;
	protected Map<String, Type> fieldType;
	
	
	public AnalysisContext() {
		threadsStatus = new HashMap<Long, Integer>();
		analyzeMethods = new HashMap<Long, Boolean>();
		dynamicMethod = new HashMap<Long, Boolean>();
		modifiedReturnExpression = new HashMap<Long, Boolean>();
		returnValue = new HashMap<Long, IExpression>();
		methodID = new HashMap<Long, String>();
		owner = new HashMap<Long, String>();
		depends = new HashMap<Long, List<Long>>();
		releasedParameters = new HashMap<Long, Map<String, String>>();
		paramString = new HashMap<Long, List<String>>();
		methodFrames = new HashMap<Long, List<BehaviourFrame>>();
		threadCounter = methodCounter = 0;
		methodNodes = new HashMap<Long, MethodNode>();
		methodBehaviour = new HashMap<Long, Map<String, IBehaviour>>();
		objectFields = new HashMap<String, List<String>>();
		fieldType = new HashMap<String, Type>();
		
		resourceClass = "java/lang/Thread";
		allocationCall = resourceClass + ".run()V";
		deallocationCall = resourceClass + ".join()V";
	}
	
	
	public void analyze(String entryPoint) throws AnalyzerException {
		long k = getKeyOfMethod(entryPoint);
		List<Long> analysisList = new ArrayList<Long>();
		ThreadAnalyzer analyzer = new ThreadAnalyzer(new ValInterpreter(this), this);
		
		analysisList.add(k);
		// reanalyze methods until a fixed point is reached
		for (int i = 0; i < analysisList.size(); ++i) {
			Long currentMethodID = analysisList.get(i);
			
			// if the method is already at fixed point don't touch it
			if (!analyzeMethods.get(currentMethodID))
				continue;

			// else, analyze it and put all its dependancies to be analyzed too.
			// also all methods which depends on it, if behaviour changes.
			// as side effect, the return value is automatically updated.
			for (String s: paramString.get(currentMethodID)) {
				BehaviourFrame[] frames = analyzer.analyze(owner.get(currentMethodID), methodNodes.get(currentMethodID), s);
	
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
				IBehaviour old = methodBehaviour.get(currentMethodID).get(s);
				IBehaviour updatedBehaviour = computeBehaviour(frames);
				
				if (!old.equalBehaviour(updatedBehaviour)) {
					methodBehaviour.get(currentMethodID).put(s, updatedBehaviour);
					for (long j = 0; ((j < methodCounter) && (j != currentMethodID)); ++j) {
						if (depends.get(j).contains(currentMethodID) && !analysisList.contains(j)) {
							analysisList.add(j);
							analyzeMethods.put(j, true);					
						}
					}
				}				
			}
			// finally, notify that we checked the method.
			analyzeMethods.put(currentMethodID, false);
		}
		
		for (long i = 0; i < methodCounter; ++i) {
			printMethodInformations(i);
		}
		
	}
	
	
	public void setAllocationVariables(String className, String alloc, String dealloc) {
		resourceClass = className;
		allocationCall = alloc;
		deallocationCall = dealloc;
	}
	
	public ThreadValue generateThread(int index) {
		ThreadValue t = new ThreadValue(new AnValue(Type.getObjectType(ThreadValue.fullyQualifiedName)),
				index >= 0 ? index : threadCounter,
				this, index >= 0, ' ');
		if (index < 0) {
			threadsStatus.put(threadCounter, ThreadResource.ALLOCATED);
			threadCounter++;
		}
		return t;
	}
	
	public ThreadResource allocateThread(ThreadValue t) {
		if (t.isVariable()) {
			return new ThreadResource(t, ThreadResource.ACQUIRE);
		} else if (threadsStatus.get(t.getID()) == ThreadResource.ALLOCATED) {
			threadsStatus.put(t.getID(), ThreadResource.ACQUIRE);
			return new ThreadResource(t, ThreadResource.ACQUIRE);			
		} else {
			return new ThreadResource(t, ThreadResource.ALREADY_ACQUIRED);
		}
	}

	public ThreadResource deallocateThread(ThreadValue t) {
		if (t.isVariable()) {
			if (threadVariableStatus.get(t.getVariableName()) != ThreadResource.RELEASE) {
				threadVariableStatus.put(t.getVariableName(), ThreadResource.RELEASE);
				return new ThreadResource(t, ThreadResource.RELEASE);
			} else {
				return new ThreadResource(t, ThreadResource.ALREADY_RELEASED);
			}
		} else if (threadsStatus.get(t.getID()) == ThreadResource.ACQUIRE) {
			threadsStatus.put(t.getID(), ThreadResource.RELEASE);
			return new ThreadResource(t, ThreadResource.RELEASE);
		} else return new ThreadResource(t, ThreadResource.ALREADY_RELEASED);
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
		
		if (returnValue.get(key) instanceof IExpression)
			return new FunctionCallExpression(returnValue.get(key).getType(), methodName);
					
		return null;
		
		// return returnValue.get(key);
	}

	public void createMethodNode(String className, String name, MethodNode method) {
		methodNodes.put(methodCounter, method);
		methodBehaviour.put(methodCounter, new HashMap<String, IBehaviour>());
		//String params = method.desc.substring(1, method.desc.indexOf(')'));
		String pString = "";
//		System.out.println("method " + className + "." + name + " has parameters:");
		for (int i = 0; i < method.localVariables.size(); ++i) {
			String d = method.localVariables.get(i).desc.replace(';', ' ').trim();
			if (d.startsWith("L"))
				d = d.substring(1);
//			System.out.println(d);
			pString += Names.alpha.charAt(i);
			if (objectFields.containsKey(d) && objectFields.get(d).size() > 0) {
				List<String> pars = objectFields.get(d);
				pString += "[";
				for (String p : pars)
					pString += p + ",";
				pString = pString.substring(0, pString.length() - 1) + "]";
			}
			if (i != method.localVariables.size() - 1)
				pString += ",";
//			System.out.println(pString);
		}
		
		methodBehaviour.get(methodCounter).put(pString, new Atom(Atom.RETURN));
		paramString.put(methodCounter, new ArrayList<String>());
		paramString.get(methodCounter).add(pString);
		owner.put(methodCounter, className);
		methodID.put(methodCounter, name);
		depends.put(methodCounter, new ArrayList<Long>());
		releasedParameters.put(methodCounter, new HashMap<String, String>());
		releasedParameters.get(methodCounter).put(pString, "");
		analyzeMethods.put(methodCounter, true);
		returnValue.put(methodCounter, new ConstExpression(Type.INT_TYPE, new Long(0)));
		modifiedReturnExpression.put(methodCounter, false);
		dynamicMethod.put(methodCounter, false);
		methodCounter++;
	}

	protected void printMethodInformations(long index) {

		String mName = methodNodes.get(index).name;
		mName = mName.substring(mName.lastIndexOf('/') + 1, mName.length());
		for (String s: paramString.get(index)) {
			/*String actualName = mName;
			for (int i = 0; i < s.length(); ++i) {
				if (i == 0 && (s.length() == 1 || s.equalsIgnoreCase("aa")))
					actualName += "(a)";
				else if (i == 0)
					actualName += "(a, ";
				else if (s.charAt(i) == Names.alpha.charAt(i))
					actualName += s.charAt(i) + ", ";
			}
			if (actualName.endsWith(", ")) {
				actualName = actualName.substring(0, actualName.lastIndexOf(", ")) + ")";
			}*/
			String actualName = mName + "(" + s + ")";
			if (actualName.equalsIgnoreCase(mName))
				actualName += "()";
			System.out.println("Method " + actualName + " has behaviour " + methodBehaviour.get(index).get(s));
			
			// TODO
			if (releasedParameters.get(index).get(s).length() >0) {
				String rels = "" + releasedParameters.get(index).get(s).charAt(0);
				for (int i = 1; i < releasedParameters.get(index).get(s).length(); ++i)
					rels += ", " + releasedParameters.get(index).get(s).charAt(i);
				System.out.println("Method " + actualName + " releases Threads " + rels);
			}
		}
		System.out.println("Method " + mName + " has return value " + returnValue.get(index));
	}

	protected IBehaviour computeBehaviour(BehaviourFrame[] frames) {
		
		return computeBehaviour(null, frames, 0, frames.length - 1);
		
	}
	
	protected IBehaviour computeBehaviour(IBehaviour start, BehaviourFrame[] frames, int begin, int end) {
		
		if (begin >= end)
			return start;
		
		IBehaviour b = frames[begin].frameBehaviour;
		if (start == null) {
			// we compute from the current behaviour.
			if (b == null)
				return computeBehaviour(start, frames, begin + 1, end);
			else {
				if (b instanceof ConditionalJump) {
					ConditionalJump con = (ConditionalJump) b;
					if (con.getThenIndex() > begin && con.getElseIndex() > begin) {
						IBehaviour thenBranch = computeBehaviour(null, frames, con.getThenIndex(), end);
						IBehaviour elseBranch = computeBehaviour(null, frames, con.getElseIndex(), end);
						con.setBranches(thenBranch, elseBranch);
						return con;
					} else return computeBehaviour(start, frames, begin + 1, end);
				} else {
					IBehaviour future = computeBehaviour(null, frames, begin + 1, end);
					if (future != null)
						return new ConcatBehaviour(b, future);
					else return b;
				}
			}
		} else {
			IBehaviour future = computeBehaviour(null, frames, begin, end);
			if (future != null)
				return new ConcatBehaviour(start, future);
			else return start;
		}
		
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

	public List<String> getParametersOf(String methodName) {
		List<String> res = new ArrayList<String>();

		MethodNode m = methodNodes.get(getKeyOfMethod(methodName));
		String d = m.desc;
		d = d.substring(1, d.lastIndexOf(")"));
		if (d.length() > 0) {
			String[] r = d.split(";");
			for (int i = 0; i < r.length; ++i)
				res.add(r[i].replace('.', '/'));
		}
		
		if (res.size() < m.localVariables.size())
			res.add(0, m.localVariables.get(0).name);
			
		return res;
	}

	public boolean isResource(String string) {
		if (string.startsWith("L"))
			return resourceClass.equalsIgnoreCase(string.substring(1));
		return resourceClass.equalsIgnoreCase(string);
	}

	public void signalRelease(String methodName, String parameterSetup, char parameter) {
		long k = getKeyOfMethod(methodName);
		String actualPars = releasedParameters.get(k).get(parameterSetup);
		if (!actualPars.contains("" + parameter))
			releasedParameters.get(k).put(parameterSetup, actualPars + parameter);
	}

	public void signalDynamicMethod(String currentMethodName) {
		dynamicMethod.put(getKeyOfMethod(currentMethodName), true);
	}

	public boolean isDynamic(String methodName) {
		return dynamicMethod.get(getKeyOfMethod(methodName));
	}

	public void signalParametersPattern(String currentMethodName,
			String paramsPattern) {
		long k = getKeyOfMethod(currentMethodName);
		if (!paramString.get(k).contains(paramsPattern)) {
			paramString.get(k).add(paramsPattern);
			releasedParameters.get(k).put(paramsPattern, "");
			methodBehaviour.get(k).put(paramsPattern, new Atom(Atom.RETURN));
			analyzeMethods.put(k, true);
		}
		
	}

	public void resetVariables() {
		threadVariableStatus = new HashMap<Character, Integer>();
	}
	
	public void newThreadVariable(char tName) {
		threadVariableStatus.put(tName, ThreadResource.DELTA);
	}

	public void newObjectVariable() {
		
	}
	
	public void signalField(String className, String name, Type type) {
		if (objectFields.containsKey(className)) {
			List<String> l = objectFields.get(className);
			if (!l.contains(name))
				l.add(name);
		} else {
			List<String> x = new ArrayList<String>();
			x.add(name);
			objectFields.put(className, x);
		}
		fieldType.put(className + "." + name, type);
	}

	public boolean typableClass(String className) {
		return objectFields.containsKey(className);
	}
	
}
