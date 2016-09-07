package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.asmTypes.ThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.VarThreadValue;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.ConstExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.FunctionCallExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.IExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.UnknownExpression;
import com.laneve.asp.ASMAnalysis.asmTypes.expressions.VarExpression;
import com.laneve.asp.ASMAnalysis.bTypes.Atom;
import com.laneve.asp.ASMAnalysis.bTypes.ConcatBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ConditionalJump;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.MethodBehaviour;
import com.laneve.asp.ASMAnalysis.bTypes.ThreadResource;
import com.laneve.asp.ASMAnalysis.utils.Names;

public class AnalysisContext {

	protected Map<String, Boolean> analyzeMethods, modifiedReturnExpression;
	protected Map<String, IExpression> returnValue;
	protected Map<String, String> methodID, owner;
	protected Map<String, List<String>> depends;
	protected Map<String, Map<String, List<String>>> releasedParameters;
	protected Map<String, List<String>> paramString;
	protected Map<String, List<BehaviourFrame>> methodFrames;
	protected Map<String, MethodNode> methodNodes;
	protected Map<String, Map<String, IBehaviour>> methodBehaviour;
	protected Map<String, List<String>> objectFields;
	protected Map<String, Type> fieldType;
	protected Map<String, Map<String, Map<String, AnValue>>> updates;
	protected Map<String, Map<String, Map<String, Integer>>> threadStatusUpdates;
	protected long threadCounter, methodCounter, objectCounter;
	protected String resourceClass, allocationCall, deallocationCall, currentSignature;
	protected Map<String, Integer> tempStatus;
	
	
	public AnalysisContext() {
		analyzeMethods = new HashMap<String, Boolean>();
		modifiedReturnExpression = new HashMap<String, Boolean>();
		returnValue = new HashMap<String, IExpression>();
		methodID = new HashMap<String, String>();
		owner = new HashMap<String, String>();
		depends = new HashMap<String, List<String>>();
		releasedParameters = new HashMap<String, Map<String, List<String>>>();
		paramString = new HashMap<String, List<String>>();
		methodFrames = new HashMap<String, List<BehaviourFrame>>();
		threadCounter = methodCounter = 0;
		methodNodes = new HashMap<String, MethodNode>();
		methodBehaviour = new HashMap<String, Map<String, IBehaviour>>();
		objectFields = new HashMap<String, List<String>>();
		fieldType = new HashMap<String, Type>();
		updates = new HashMap<String, Map<String, Map<String,AnValue>>>();
		threadStatusUpdates = new HashMap<String, Map<String, Map<String, Integer>>>();
		tempStatus = new HashMap<String, Integer>();
		
		objectCounter = 0;
		resourceClass = "java/lang/Thread";
		allocationCall = resourceClass + ".run()V";
		deallocationCall = resourceClass + ".join()V";
	}
	
	
	public void analyze(String entryPoint) throws AnalyzerException {
		
		List<AnValue> startingParameters = new ArrayList<AnValue>();
		MethodNode m = methodNodes.get(entryPoint);
		int par = 0;
		if ((m.access & Opcodes.ACC_STATIC) == 0) {
			startingParameters.add(newObjectVariable(Type.getObjectType(owner.get(entryPoint)), par, Names.get(par)));
			par ++;
		}
        Type[] args = Type.getArgumentTypes(m.desc);
        for (int i = 0; i < args.length; ++i) {
        	startingParameters.add(newObjectVariable(args[i], par, Names.get(par)));
			par ++;
        }
        
        signalParametersPattern(entryPoint, Names.computeParameterList(startingParameters));
		
		List<String> analysisList = new ArrayList<String>();
		ThreadAnalyzer analyzer = new ThreadAnalyzer(new ValInterpreter(this), this);
		
		analysisList.add(entryPoint);
		// reanalyze methods until a fixed point is reached
		for (int i = 0; i < analysisList.size(); ++i) {
			String currentMethodID = analysisList.get(i);
			
			// if the method is already at fixed point don't touch it
			if (!analyzeMethods.get(currentMethodID))
				continue;

//			System.out.println("Analyzing " + currentMethodID);
			// else, analyze it and put all its dependancies to be analyzed too.
			// also all methods which depends on it, if behaviour changes.
			// as side effect, the return value is automatically updated.
			analyzeMethods.put(currentMethodID, false);
			for (String s: paramString.get(currentMethodID)) {
				
				currentSignature = s;
//				System.out.println("Analyzing variant " + s);
				BehaviourFrame[] frames = analyzer.analyze(owner.get(currentMethodID), methodNodes.get(currentMethodID), s);
	
//				System.out.println("Analysis ended.");
				for (String j: depends.get(currentMethodID)) {
					// we put on all its dependancies
					analysisList.add(j);
				}
				for (String j : methodBehaviour.keySet()) {
					// ... and all methods depending on it.
					if (depends.get(j).contains(currentMethodID))
							analysisList.add(j);
				}
				
				// if the return value was updated, also examine all methods depending on this.
				if (modifiedReturnExpression.get(currentMethodID)) {
					//System.out.println("Since the method return value was modified, we also reanalyze:");
					for (String j : methodBehaviour.keySet()) {
						if (depends.get(j).contains(currentMethodID)) {
							analysisList.add(j);
							analyzeMethods.put(j, true);
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
					for (String j : methodBehaviour.keySet()) {
						if (depends.get(j).contains(currentMethodID) && !analysisList.contains(j) && !j.equalsIgnoreCase(currentMethodID)) {
							analysisList.add(j);
							analyzeMethods.put(j, true);					
						}
					}
				}				
			}
		}
		
		for (String s : methodBehaviour.keySet()) {
			printMethodInformations(s);
		}
		
	}
	
	
	public void setAllocationVariables(String className, String alloc, String dealloc) {
		resourceClass = className;
		allocationCall = alloc;
		deallocationCall = dealloc;
	}
	
	public ThreadResource allocateThread(ThreadValue t) {
//		System.out.println("Context request: run thread #" + t.getThreadID() + " with status " + t.getStatus());
		if (t instanceof VarThreadValue) {

			if (t.getStatus() == ThreadResource.ALLOCATED) {
//				System.out.println("Thread #" + t.getThreadID() + " has new status " + t.getStatus());
				t.setUpdated(true);
				return new ThreadResource(t, ThreadResource.ACQUIRE);
			} else {
				return new ThreadResource(t, ThreadResource.ALREADY_ACQUIRED);
			}
		} else if (t.getStatus() == ThreadResource.ALLOCATED) {
			t.setUpdated(true);
			return new ThreadResource(t, ThreadResource.ACQUIRE);
		} else return new ThreadResource(t, ThreadResource.ALREADY_ACQUIRED);
	}

	public ThreadResource deallocateThread(ThreadValue t) {
//		System.out.println("Context request: deallocate thread #" + t.getThreadID() + " with status " + t.getStatus());
		if (t instanceof VarThreadValue) {
			if (t.getStatus() == ThreadResource.ALREADY_ACQUIRED) {
				t.setUpdated(true);
//				System.out.println("New thread status: " + t.getStatus());
				return new ThreadResource(t, ThreadResource.RELEASE);
			} else {
				return new ThreadResource(t, ThreadResource.ALREADY_RELEASED);
			}
		} else if (t.getStatus() == ThreadResource.ALREADY_ACQUIRED) {
			t.setUpdated(true);
			return new ThreadResource(t, ThreadResource.RELEASE);
		} else return new ThreadResource(t, ThreadResource.ALREADY_RELEASED);
	}

	public void signalDependancy(String methodName, List<String> deps) {
		for (String s: deps) {
			
			if (!methodID.values().contains(s))
				continue;

			// add the index of methods.
			if (!depends.get(methodName).contains(s)) {
				depends.get(methodName).add(s);
			}
		}
			
	}
	
	public void modified(String method) {
		for (String s : methodBehaviour.keySet()) {
			if (depends.get(s).contains(method)) {
				analyzeMethods.put(s, true);
			}
		}
	}
	
	public void setReturnExpression(String method, AnValue value) {

		if (returnValue.get(method).equalValue(new ConstExpression(Type.INT_TYPE, 0L))
			&& !value.equalValue(new ConstExpression(Type.INT_TYPE, 0L))) {
			returnValue.put(method, (IExpression) value);
			modifiedReturnExpression.put(method, true);
			//System.out.println("Method " + method + " was modified: new return value is " + value.toString());
		} else if (returnValue.get(method).equalValue(new UnknownExpression(value.getType())))
			return;
		else if (!returnValue.get(method).equalValue((IExpression)value)) {
			returnValue.put(method, new UnknownExpression(value.getType()));
			modifiedReturnExpression.put(method, true);			
		}
		
	}
		
	public IExpression getReturnValueOfMethod(String methodName) {
		// All foreign methods are treated as null. actual usage of this value will result in cast errors.
		
		if (returnValue.get(methodName) instanceof IExpression)
			return new FunctionCallExpression(returnValue.get(methodName).getType(), methodName);
					
		return null;
		
		// return returnValue.get(key);
	}

	public void createMethodNode(String className, String name, MethodNode method) {
		String m = name;
		methodNodes.put(m, method);
		methodBehaviour.put(m, new HashMap<String, IBehaviour>());		
		paramString.put(m, new ArrayList<String>());
		owner.put(m, className);
		methodID.put(m, name);
		depends.put(m, new ArrayList<String>());
		releasedParameters.put(m, new HashMap<String, List<String>>());
		analyzeMethods.put(m, true);
		returnValue.put(m, new ConstExpression(Type.INT_TYPE, new Long(0)));
		modifiedReturnExpression.put(m, false);
		updates.put(m, new HashMap<String, Map<String, AnValue>>());
		threadStatusUpdates.put(m, new HashMap<String, Map<String, Integer>>());
		methodCounter++;
	}
	
	protected void printMethodInformations(String index) {

		String mName = owner.get(index).substring(owner.get(index).lastIndexOf('/') + 1) + "." + methodNodes.get(index).name;
		for (String s: paramString.get(index)) {
			String actualName = mName + "(" + s + ")";
			System.out.println("Method " + actualName + " has behaviour " + methodBehaviour.get(index).get(s));
			
			if (releasedParameters.get(index).get(s).size() > 0) {
				String rels = releasedParameters.get(index).get(s).get(0);
				for (int i = 1; i < releasedParameters.get(index).get(s).size(); ++i)
					rels += ", " + releasedParameters.get(index).get(s).get(i);
				System.out.println("Method " + actualName + " releases Threads " + rels);
			}
			if (updates.get(index).get(s).size() > 0) {
				String update = "";
				for (Entry<String, AnValue> e: updates.get(index).get(s).entrySet())
					update += e.getKey() + ": " + e.getValue() + "; ";
				System.out.println("Method " + actualName + " updates fields " + update);
			}
			if (threadStatusUpdates.get(index).get(s).size() > 0) {
				String update = "";
				for (Entry<String, Integer> e: threadStatusUpdates.get(index).get(s).entrySet())
					update += e.getKey() + ": " + e.getValue() + "; ";
				System.out.println("Method " + actualName + " updates threads status " + update);
			}
		}
		System.out.println("Method " + mName + " has return value " + returnValue.get(index));
	}

	protected IBehaviour computeBehaviour(BehaviourFrame[] frames) {
		
		IBehaviour b = computeBehaviour(null, frames, 0, frames.length - 1);
		return b == null ? new Atom(Atom.RETURN) : b;
		
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
		//return methodBehaviour.get(currentMethodName);
	}

	public List<String> getParametersOf(String methodName) {
		List<String> res = new ArrayList<String>();

		MethodNode m = methodNodes.get(methodName);
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
		return resourceClass.equalsIgnoreCase(Names.normalizeClassName(string));
	}

	public boolean typableMethod(String currentMethodName) {
		return methodID.containsValue(currentMethodName);
	}
	
	public boolean typableClass(String className) {
		return objectFields.containsKey(className) || objectFields.containsKey(className.replace('.', '/'));
	}

	public void signalRelease(String methodName, String parameterSetup, String parameter) {
		System.out.println("Method " + methodName + parameterSetup + " releases " + parameter);
		List<String> actualPars = releasedParameters.get(methodName).get(parameterSetup);
		if (!actualPars.contains(parameter))
			releasedParameters.get(methodName).get(parameterSetup).add(parameter);
	}

	public void signalParametersPattern(String currentMethodName,
			String paramsPattern) {
		String k = currentMethodName;
		if (!paramString.get(k).contains(paramsPattern)) {
			paramString.get(k).add(paramsPattern);
			releasedParameters.get(k).put(paramsPattern, new ArrayList<String>());
			methodBehaviour.get(k).put(paramsPattern, new Atom(Atom.RETURN));
			analyzeMethods.put(k, true);
			updates.get(k).put(paramsPattern, new HashMap<String, AnValue>());
			Map<String, Integer> startStatus = new HashMap<String, Integer>();
			parseStatus(paramsPattern, startStatus);
			
			threadStatusUpdates.get(k).put(paramsPattern, startStatus);
		}
		
	}
	
	protected void parseStatus(String pattern, Map<String, Integer> status) {
		
		if (!pattern.contains(":"))
			return;
		
		if (!pattern.contains(",")) {
			// single parameter
			if (pattern.contains("[") || pattern.contains("]")) {
				int begin = (pattern.contains("[") ? pattern.indexOf("[") + 1 : 0);
				int end = (pattern.contains("]") ? pattern.indexOf("]") : 0);
				parseStatus(pattern.substring(begin, end), status);
			} else {
				String name = pattern.split(":")[0];
				int value = Integer.parseInt(pattern.split(":")[1]);
				if (!status.containsKey(name))
					status.put(name, value);
			}
		} else {
			for (String s : Arrays.asList(pattern.split(",")))
				parseStatus(s, status);
		}
		return;
			
		
		
/*		if (!(pattern.contains(",") || pattern.contains("["))) {
			if (pattern.contains(":")) {
				String name = pattern.split(":")[0], statusString = pattern.split(":")[1];
				if (!status.containsKey(name))
					status.put(name, Integer.parseInt(statusString));
			}
		} else {
			if (!pattern.contains("[") || pattern.indexOf(',') < pattern.indexOf('[')) {
				// nomi normali
				System.out.println(pattern + " caused stack overflow");
				for (String s: Names.getSingleParameters(pattern))
					parseStatus(s, status);
			} else {
				// singolo parametro con campi
				String subpattern = pattern.substring(pattern.indexOf('[') + 1, pattern.length() - 1);
				for (String s: Names.getSingleParameters(subpattern))
					parseStatus(s, status);
			}
		}*/
	}
	
	public AnValue newObjectVariable(Type t, int p, String oName) {
		if (t == Type.INT_TYPE || t == Type.LONG_TYPE) {
			return new VarExpression(t, p, oName);
		} else if (isResource(t.getClassName())) {
			String[] x = oName.split(":");
			String n = x[0];
			int status = Integer.parseInt(x[1]);
			return generateVarThread(n, p, status);
		}

		
		String name = oName;
		if (name.contains("["))
			name = name.substring(name.indexOf("["), name.length() - 1);
		AnValue a = new AnValue(t, name);
		a.setVariable(true);
		if (!typableClass(t.getClassName()))
			return a;

		String oType = t.getClassName();
		List<String> fields = objectFields.get(oType);
		if (fields != null)
			for (String f : fields) {
				Type fType = fieldType.get(oType + "." + f);
				a.setField(f, newObjectVariable(fType, p, f));
			}
		
		a.setUpdated(false);
		return a;
	}
	
	public AnValue newObject(Type t) {		
		return newObject(t, "o" + objectCounter++);
	}
	
	protected AnValue newObject(Type t, String name) {

		if (t == Type.INT_TYPE || t == Type.LONG_TYPE) {
			ConstExpression x = new ConstExpression(t, new Long(0));
			//x.setName(name);
			return x;
		} else if (isResource(t.getClassName()))
			return generateThread(name, ThreadResource.ALLOCATED);
		
		AnValue a = new AnValue(t, name);
		
		String nclassName = Names.normalizeClassName(t.getClassName());
		if (typableClass(nclassName))			
			for (String field : objectFields.get(nclassName)) {
 				String fieldName = nclassName + "." + field;
				a.setField(field, newObject(fieldType.get(fieldName), nclassName + "." + field));
			}
		a.setUpdated(false);
		return a;
	}
	
	protected ThreadValue generateThread(String oName, int status) {
		ThreadValue thr = new ThreadValue(new AnValue(Type.getObjectType(ThreadValue.fullyQualifiedName)),
				threadCounter, status, false, oName);
		threadCounter++;
		return thr;
	}

	protected VarThreadValue generateVarThread(String oName, int pos, int status) {
		VarThreadValue thr = new VarThreadValue(new AnValue(Type.getObjectType(ThreadValue.fullyQualifiedName)),
				threadCounter, status, oName, pos);
		threadCounter++;
		return thr;
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

	
	public AnValue parseObjectVariable(Type ctype, int pos, String parameter, Map<String, AnValue> parameterValues) {
		String name = (!parameter.contains("[") ? parameter : parameter.substring(0, parameter.indexOf("[")));
		if (parameterValues.containsKey(name))
			return parameterValues.get(name);

		if (isResource(ctype.getClassName())) {
			String n = name.split(":")[0];
			int status = Integer.parseInt(name.split(":")[1]);
			ThreadValue v = generateVarThread(n, pos, status);
			parameterValues.put(n, v);
			return v;
		} else if (name.contains(":unknown"))
			return new UnknownExpression(ctype, name.substring(0, name.indexOf(':')));
		
		AnValue baseObject = newObjectVariable(ctype, pos, name);
		if (!name.equalsIgnoreCase(parameter)) {
			List<String> fields = Names.getSingleParameters(parameter.substring(parameter.indexOf("[") + 1, parameter.lastIndexOf("]")));
		
			String clName = Names.normalizeClassName(ctype.getClassName());
			if (typableClass(clName)) {
				List<String> fieldNames = objectFields.get(clName);
				if (fieldNames != null)
					for (int i = 0; i < fieldNames.size(); ++i) {
						Type f = fieldType.get(clName + "." + fieldNames.get(i));
						baseObject.setField(fieldNames.get(i),
								parseObjectVariable(f, pos, fields.get(i), parameterValues));
					}
			}
		}
		baseObject.setUpdated(false);
		parameterValues.put(name, baseObject);
		return baseObject;
	}

	public Map<Long, Map<String, AnValue>> computeUpdatesToLocalEnvironment(String method, String signature, List<AnValue> parameters) {
		if (method.contains("swap"))
			signature.length();
		
		Map<Long, Map<String, AnValue>> updatesByID = new HashMap<Long, Map<String, AnValue>>();
		if (!methodID.containsValue(method))
			return updatesByID;
		Map<String, AnValue> up = updates.get(method).get(signature);
		for (int i = 0; i < parameters.size(); ++i)
			applyUpdates(parameters, i, up, updatesByID);
		return updatesByID;
	}
	
	private void applyUpdates(List<AnValue> parameters, int i, Map<String, AnValue> up, Map<Long, Map<String, AnValue>> resultingUpdates) {
		// this must not be analyzed if the same object has already been updated;
		if (resultingUpdates.containsKey(parameters.get(i).getID()))
			return;
		
		if (parameters.get(i) instanceof ThreadValue || parameters.get(i) instanceof IExpression) {
			return;
		}
		
		// and if no field of this object must be modified we return.
		boolean isThere = false;
		for (String x: up.keySet())
			if (x.startsWith(Names.get(i))) {
				isThere = true;
				break;
			}
		if (!isThere)
			return;
		
		// finally, if the parameter is just a thread which status must be updated signal its new status.
		Map<String, AnValue> tempMap = new HashMap<String, AnValue>();
		for (String updatedField : up.keySet())
			if (updatedField.equalsIgnoreCase(Names.get(i)) &&  parameters.get(i) instanceof ThreadValue) {
				
				ThreadValue x = ((ThreadValue)parameters.get(i));
				if ((ThreadValue)up.get(Names.get(i)) instanceof VarThreadValue) {
					x = ((VarThreadValue)up.get(Names.get(i))).compute(parameters);
					tempMap.put(updatedField, x);
				}
				else {
					ThreadValue t = generateThread("t" + threadCounter, ((ThreadValue)up.get(Names.get(i))).getStatus());
					//getStatusOfThread(((ThreadValue)up.get(Names.get(i))).getThreadID()));
					tempMap.put(updatedField, t);
				}
				x.cloneStatus((ThreadValue)up.get(Names.get(i)));
			} else if (updatedField.startsWith(Names.get(i))) {
				String fieldName = updatedField.substring(updatedField.indexOf('.') + 1);
				AnValue val = up.get(updatedField);
				if (val instanceof ConstExpression)
					tempMap.put(fieldName, val);
				else if (val instanceof IExpression) {
					IExpression x = ((IExpression) val).evaluate(parameters);
					tempMap.put(fieldName, x);
				} else if (val instanceof VarThreadValue) {
					ThreadValue x = ((VarThreadValue)val).compute(parameters);
					tempMap.put(fieldName, x);
					x.cloneStatus((ThreadValue)val);
				} else if (val instanceof ThreadValue) {
					ThreadValue t = generateThread("t" + threadCounter, ((ThreadValue)val).getStatus());
					tempMap.put(fieldName, t);
				}
			}
		if (tempMap.size() > 0)
			resultingUpdates.put(parameters.get(i).getID(), tempMap);
	}

	public void signalFinalState(String methodName, List<AnValue> localList) {

//		System.out.println("method " + methodName + currentSignature + " has final update status " + tempStatus);
		// get the actual number of parameters..
		int paramSize = Names.getSingleParameters(currentSignature).size();
		Map<String, AnValue> m = new HashMap<String, AnValue>();
		
		for (int i = 0; i < paramSize; ++i)
			computeUpdates(localList.get(i), m, Names.get(i));
		
		//..and save them in the apposite section.
		Map<String, Map<String, AnValue>> old = updates.get(methodName);
		
		// cases when something was updated: 
		// 1) No updates so far;
		if (old == null)
			modified(methodName);
		// 2) No updates for current signature;
		else if (old.get(currentSignature) == null)
			modified(methodName);
		// 3) different updates for current signature:
		else {
			boolean mod = false;
			Map<String, AnValue> oldInner = old.get(currentSignature); 
			for (Entry<String, AnValue> e : m.entrySet()) {
				// 3.1) different keyset (newer is larger);
				if (!oldInner.containsKey(e.getKey())) {
					mod = true;
					break;
				// 3.2) different values for keyset (newer is more accurate).
				} else if (!oldInner.get(e.getKey()).equalValue(e.getValue())) {
					mod = true;
					break;
				}
			}
			
			if (threadStatusUpdates.get(methodName).get(currentSignature) == null) 
				mod = true;
			else {
				Map<String, Integer> comp = threadStatusUpdates.get(methodName).get(currentSignature);
				for (Entry<String, Integer> e : tempStatus.entrySet()) {
					if (!comp.containsKey(e.getKey())) {
						mod = true;
						break;
					} else if (comp.get(e.getKey()).intValue() != e.getValue().intValue()) {
						mod = true;
						break;
					}
				}
			}
			
			if (mod)
				modified(methodName);
		}
		updates.get(methodName).put(currentSignature, m);
		threadStatusUpdates.get(methodName).put(currentSignature, new HashMap<String, Integer>());
		for (Entry<String, Integer> e : tempStatus.entrySet()) {
			threadStatusUpdates.get(methodName).get(currentSignature).put(e.getKey(), e.getValue());
		}
		
		tempStatus = new HashMap<String, Integer>();
	}

	private void computeUpdates(AnValue a, Map<String, AnValue> m, String fatherName) {
		if (a instanceof IExpression && a.updated() && fatherName != null)
			m.put(a.getFieldName(), a.clone());
		else if (a instanceof ThreadValue) {
			if (a.getFieldName() != null)
				m.put(a.getFieldName(), a.clone());
			else
				m.put(fatherName, a);
		} else if (a.getFieldSize() > 0) {
			String n = (a.getFieldName() == null ? a.getName() : a.getFieldName());
			for (AnValue x : a.getFields())
				computeUpdates(x, m, n);
		}
	}

	public Map<Long, Map<String, AnValue>> getAtomicUpdate(AnValue anValue, String currentMethodName) {
		
		ThreadValue t = (ThreadValue)anValue;
		Map<Long, Map<String, AnValue>> r = new HashMap<Long, Map<String, AnValue>>();
		Map<String, AnValue> r1 = new HashMap<String, AnValue>();

		if (allocationCall.equalsIgnoreCase(currentMethodName))
			t.runThread();
		else
			t.joinThread();
		
		r1.put("t", t);
		r.put(t.getID(), r1);
		
		return r;
	}

	public void signalNewStatus(String methodName, String methodParametersPattern, String variableName,
			int alreadyAcquired) {
//		System.out.println("Method " + methodName + methodParametersPattern + " sets thread " + variableName + " to " + alreadyAcquired);
		tempStatus.put(variableName, alreadyAcquired);
	}
	
	public Map<String, Integer> getThreadUpdates(String currentMethodName, String methodParametersPattern) {
		return threadStatusUpdates.get(currentMethodName).get(methodParametersPattern);
	}


}
