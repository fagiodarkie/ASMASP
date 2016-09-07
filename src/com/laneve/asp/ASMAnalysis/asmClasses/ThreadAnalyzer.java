package com.laneve.asp.ASMAnalysis.asmClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import com.laneve.asp.ASMAnalysis.asmTypes.AnValue;
import com.laneve.asp.ASMAnalysis.bTypes.IBehaviour;
import com.laneve.asp.ASMAnalysis.utils.Names;

public class ThreadAnalyzer implements Opcodes {

	protected AnalysisContext context;
	protected String methodName;

	private final ValInterpreter interpreter;

    private int n;

    private InsnList insns;

    private List<TryCatchBlockNode>[] handlers;

    private BehaviourFrame[] frames;

    private OwnedSubroutine[] subroutines;

    private boolean[] queued;

    private int[] queue;

    private int top;

    public ThreadAnalyzer(final ValInterpreter interpreter, AnalysisContext c) {
        this.interpreter = interpreter;
        context = c;
    }

    @SuppressWarnings("unchecked")
    public BehaviourFrame[] analyzePrivately(final String owner, final MethodNode m, final String parameters)
            throws AnalyzerException {
        if ((m.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            frames = (BehaviourFrame[]) new BehaviourFrame[0];
            return frames;
        }
        n = m.instructions.size();
        insns = m.instructions;
        handlers = (List<TryCatchBlockNode>[]) new List<?>[n];
        frames = (BehaviourFrame[]) new BehaviourFrame[n];
        subroutines = new OwnedSubroutine[n];
        queued = new boolean[n];
        queue = new int[n];
        top = 0;

        // computes exception handlers for each instruction
        for (int i = 0; i < m.tryCatchBlocks.size(); ++i) {
            TryCatchBlockNode tcb = m.tryCatchBlocks.get(i);
            int begin = insns.indexOf(tcb.start);
            int end = insns.indexOf(tcb.end);
            for (int j = begin; j < end; ++j) {
                List<TryCatchBlockNode> insnHandlers = handlers[j];
                if (insnHandlers == null) {
                    insnHandlers = new ArrayList<TryCatchBlockNode>();
                    handlers[j] = insnHandlers;
                }
                insnHandlers.add(tcb);
            }
        }

        // computes the subroutine for each instruction:
        OwnedSubroutine main = new OwnedSubroutine(null, m.maxLocals, null);
        List<AbstractInsnNode> subroutineCalls = new ArrayList<AbstractInsnNode>();
        Map<LabelNode, OwnedSubroutine> subroutineHeads = new HashMap<LabelNode, OwnedSubroutine>();
        findSubroutine(0, main, subroutineCalls);
        while (!subroutineCalls.isEmpty()) {
            JumpInsnNode jsr = (JumpInsnNode) subroutineCalls.remove(0);
            OwnedSubroutine sub = subroutineHeads.get(jsr.label);
            if (sub == null) {
                sub = new OwnedSubroutine(jsr.label, m.maxLocals, jsr);
                subroutineHeads.put(jsr.label, sub);
                findSubroutine(insns.indexOf(jsr.label), sub, subroutineCalls);
            } else {
                sub.callers.add(jsr);
            }
        }
        for (int i = 0; i < n; ++i) {
            if (subroutines[i] != null && subroutines[i].start == null) {
                subroutines[i] = null;
            }
        }

        // initializes the data structures for the control flow analysis
        BehaviourFrame current = newFrame(m.maxLocals, m.maxStack);
        BehaviourFrame handler = newFrame(m.maxLocals, m.maxStack);
        current.setReturn(interpreter.newValue(Type.getReturnType(m.desc)));
        current.setParameterPattern(parameters);
        handler.setParameterPattern(parameters);
        Type[] args = Type.getArgumentTypes(m.desc);
        int local = 0, object = 0;
        List<String> singleParameters = Names.getSingleParameters(parameters);//Arrays.asList(parameters.split(","));
        
        Map<String, AnValue> parameterValues = new HashMap<String, AnValue>();
        
        if ((m.access & ACC_STATIC) == 0) {
    		Type ctype = Type.getObjectType(owner);
            current.setLocal(local++, context.parseObjectVariable(ctype, 0, singleParameters.get(0), parameterValues));
            object = 1;
            //singleParameters.remove(0);
            //context.signalDynamicMethod(methodName);
        }
        for (int i = 0; i < args.length; ++i) {
            current.setLocal(local++, context.parseObjectVariable(args[i], i + object, singleParameters.get(i + object), parameterValues));
            if (args[i].getSize() == 2) {
                current.setLocal(local++, interpreter.newValue(null));
            }
        }
        while (local < m.maxLocals) {
            current.setLocal(local++, interpreter.newValue(null));
        }
        merge(0, current, null);

        init(owner, m);

        // control flow analysis
        while (top > 0) {
            int insn = queue[--top];
            
            
            BehaviourFrame f = frames[insn];
            OwnedSubroutine subroutine = subroutines[insn];
            queued[insn] = false;

            AbstractInsnNode insnNode = null;
            try {
                insnNode = m.instructions.get(insn);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();

                if (methodName.contains("fact")) {
                	insn += 0;
                }
                if (insnType == AbstractInsnNode.LABEL
                        || insnType == AbstractInsnNode.LINE
                        || insnType == AbstractInsnNode.FRAME) {
                	// here was merge
                    mergeCopy(insn + 1, f, subroutine);
                    newControlFlowEdge(insn, insn + 1);
                } else {
                	if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode j = (JumpInsnNode) insnNode;
                        int jump = insns.indexOf(j.label);
                        current.init(f).executeJump(j, interpreter, insn, insn + 1, jump);
                        frames[insn].setBehaviour(current.getBehaviour());
                	}
                    else {
                    	current.init(f).execute(insnNode, interpreter);
                    }
                    subroutine = subroutine == null ? null : subroutine.copy();

                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode j = (JumpInsnNode) insnNode;
                        if (insnOpcode != GOTO && insnOpcode != JSR) {
                            mergeNoBehaviour(insn + 1, current, subroutine);
                            newControlFlowEdge(insn, insn + 1);
                        }
                        int jump = insns.indexOf(j.label);
                        if (insnOpcode == JSR) {
                            mergeNoBehaviour(jump, current, new OwnedSubroutine(j.label,
                                    m.maxLocals, j));
                        } else {
                            mergeNoBehaviour(jump, current, subroutine);
                        }
                        newControlFlowEdge(insn, jump);
                    } else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lsi = (LookupSwitchInsnNode) insnNode;
                        int jump = insns.indexOf(lsi.dflt);
                        merge(jump, current, subroutine);
                        newControlFlowEdge(insn, jump);
                        for (int j = 0; j < lsi.labels.size(); ++j) {
                            LabelNode label = lsi.labels.get(j);
                            jump = insns.indexOf(label);
                            merge(jump, current, subroutine);
                            newControlFlowEdge(insn, jump);
                        }
                    } else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tsi = (TableSwitchInsnNode) insnNode;
                        int jump = insns.indexOf(tsi.dflt);
                        merge(jump, current, subroutine);
                        newControlFlowEdge(insn, jump);
                        for (int j = 0; j < tsi.labels.size(); ++j) {
                            LabelNode label = tsi.labels.get(j);
                            jump = insns.indexOf(label);
                            merge(jump, current, subroutine);
                            newControlFlowEdge(insn, jump);
                        }
                    } else if (insnOpcode == RET) {
                        if (subroutine == null) {
                            throw new AnalyzerException(insnNode,
                                    "RET instruction outside of a sub routine");
                        }
                        for (int i = 0; i < subroutine.callers.size(); ++i) {
                            JumpInsnNode caller = subroutine.callers.get(i);
                            int call = insns.indexOf(caller);
                            if (frames[call] != null) {
                                merge(call + 1, frames[call], current,
                                        subroutines[call], subroutine.access);
                                newControlFlowEdge(insn, call + 1);
                            }
                        }
                    } else if (insnOpcode != ATHROW
                            && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                        if (subroutine != null) {
                            if (insnNode instanceof VarInsnNode) {
                                int var = ((VarInsnNode) insnNode).var;
                                subroutine.access[var] = true;
                                if (insnOpcode == LLOAD || insnOpcode == DLOAD
                                        || insnOpcode == LSTORE
                                        || insnOpcode == DSTORE) {
                                    subroutine.access[var + 1] = true;
                                }
                            } else if (insnNode instanceof IincInsnNode) {
                                int var = ((IincInsnNode) insnNode).var;
                                subroutine.access[var] = true;
                            }
                        }
                        merge(insn + 1, current, subroutine);
                        newControlFlowEdge(insn, insn + 1);
                    }
                }

                List<TryCatchBlockNode> insnHandlers = handlers[insn];
                if (insnHandlers != null) {
                    for (int i = 0; i < insnHandlers.size(); ++i) {
                        TryCatchBlockNode tcb = insnHandlers.get(i);
                        Type type;
                        if (tcb.type == null) {
                            type = Type.getObjectType("java/lang/Throwable");
                        } else {
                            type = Type.getObjectType(tcb.type);
                        }
                        int jump = insns.indexOf(tcb.handler);
                        if (newControlFlowExceptionEdge(insn, tcb)) {
                            handler.init(f);
                            handler.clearStack();
                            handler.push(interpreter.newValue(type));
                            merge(jump, handler, subroutine);
                        }
                    }
                }
            } catch (AnalyzerException e) {
                throw new AnalyzerException(e.node, "Error at instruction "
                        + insn + ": " + e.getMessage(), e);
            } catch (Exception e) {
                throw new AnalyzerException(insnNode, "Error at instruction "
                        + insn + ": " + e.getMessage(), e);
            }
        }

        return frames;
    }

	private void findSubroutine(int insn, final OwnedSubroutine sub,
            final List<AbstractInsnNode> calls) throws AnalyzerException {
        while (true) {
            if (insn < 0 || insn >= n) {
                throw new AnalyzerException(null,
                        "Execution can fall off end of the code");
            }
            if (subroutines[insn] != null) {
                return;
            }
            subroutines[insn] = sub.copy();
            AbstractInsnNode node = insns.get(insn);

            // calls findSubroutine recursively on normal successors
            if (node instanceof JumpInsnNode) {
                if (node.getOpcode() == JSR) {
                    // do not follow a JSR, it leads to another subroutine!
                    calls.add(node);
                } else {
                    JumpInsnNode jnode = (JumpInsnNode) node;
                    findSubroutine(insns.indexOf(jnode.label), sub, calls);
                }
            } else if (node instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode tsnode = (TableSwitchInsnNode) node;
                findSubroutine(insns.indexOf(tsnode.dflt), sub, calls);
                for (int i = tsnode.labels.size() - 1; i >= 0; --i) {
                    LabelNode l = tsnode.labels.get(i);
                    findSubroutine(insns.indexOf(l), sub, calls);
                }
            } else if (node instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode lsnode = (LookupSwitchInsnNode) node;
                findSubroutine(insns.indexOf(lsnode.dflt), sub, calls);
                for (int i = lsnode.labels.size() - 1; i >= 0; --i) {
                    LabelNode l = lsnode.labels.get(i);
                    findSubroutine(insns.indexOf(l), sub, calls);
                }
            }

            // calls findSubroutine recursively on exception handler successors
            List<TryCatchBlockNode> insnHandlers = handlers[insn];
            if (insnHandlers != null) {
                for (int i = 0; i < insnHandlers.size(); ++i) {
                    TryCatchBlockNode tcb = insnHandlers.get(i);
                    findSubroutine(insns.indexOf(tcb.handler), sub, calls);
                }
            }

            // if insn does not falls through to the next instruction, return.
            switch (node.getOpcode()) {
            case GOTO:
            case RET:
            case TABLESWITCH:
            case LOOKUPSWITCH:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
            case ATHROW:
                return;
            }
            insn++;
        }
    }

    /**
     * Returns the symbolic stack frame for each instruction of the last
     * recently analyzed method.
     * 
     * @return the symbolic state of the execution stack frame at each bytecode
     *         instruction of the method. The size of the returned array is
     *         equal to the number of instructions (and labels) of the method. A
     *         given frame is <tt>null</tt> if the corresponding instruction
     *         cannot be reached, or if an error occured during the analysis of
     *         the method.
     */
    public BehaviourFrame[] getFrames() {
        return frames;
    }

    /**
     * Returns the exception handlers for the given instruction.
     * 
     * @param insn
     *            the index of an instruction of the last recently analyzed
     *            method.
     * @return a list of {@link TryCatchBlockNode} objects.
     */
    public List<TryCatchBlockNode> getHandlers(final int insn) {
        return handlers[insn];
    }


    protected void init(String owner, MethodNode m) throws AnalyzerException {
    }

    protected void newControlFlowEdge(final int insn, final int successor) {
    }

    protected boolean newControlFlowExceptionEdge(final int insn,
            final int successor) {
        return true;
    }

    protected boolean newControlFlowExceptionEdge(final int insn,
            final TryCatchBlockNode tcb) {
        return newControlFlowExceptionEdge(insn, insns.indexOf(tcb.handler));
    }

    // -------------------------------------------------------------------------

    private void mergeCopy(final int insn, final BehaviourFrame frame,
            final OwnedSubroutine subroutine) throws AnalyzerException {
    	merge(insn, frame, subroutine);
    	frames[insn].setBehaviour(null);
    }
    
    private void merge(final int insn, final BehaviourFrame frame,
            final OwnedSubroutine subroutine) throws AnalyzerException {
        BehaviourFrame oldFrame = frames[insn];
        OwnedSubroutine oldSubroutine = subroutines[insn];
        boolean changes;

        if (oldFrame == null) {
            frames[insn] = newFrame(frame);
            changes = true;
        } else {
            changes = oldFrame.merge(frame, interpreter);
        }

        if (oldSubroutine == null) {
            if (subroutine != null) {
                subroutines[insn] = subroutine.copy();
                changes = true;
            }
        } else {
            if (subroutine != null) {
                changes |= oldSubroutine.merge(subroutine);
            }
        }
        if (changes && !queued[insn]) {
            queued[insn] = true;
            queue[top++] = insn;
        }
    }

    private void mergeNoBehaviour(final int insn, final BehaviourFrame frame,
            final OwnedSubroutine subroutine) throws AnalyzerException {
    	merge(insn, frame, subroutine);
    	frames[insn].resetBehaviour();
    }
    
    private void merge(final int insn, final BehaviourFrame beforeJSR,
            final BehaviourFrame afterRET, final OwnedSubroutine subroutineBeforeJSR,
            final boolean[] access) throws AnalyzerException {
        BehaviourFrame oldFrame = frames[insn];
        OwnedSubroutine oldSubroutine = subroutines[insn];
        boolean changes;

        afterRET.merge(beforeJSR, access);

        if (oldFrame == null) {
            frames[insn] = newFrame(afterRET);
            changes = true;
        } else {
            changes = oldFrame.merge(afterRET, interpreter);
        }

        if (oldSubroutine != null && subroutineBeforeJSR != null) {
            changes |= oldSubroutine.merge(subroutineBeforeJSR);
        }
        if (changes && !queued[insn]) {
            queued[insn] = true;
            queue[top++] = insn;
        }
    }
    
	public BehaviourFrame[] analyze(final String owner, final MethodNode m, String s)
            throws AnalyzerException {
		
		methodName = owner + "." + m.name + m.desc;
		
		/*
		 * The analyze() method is able to compute actual BehaviourFrames, due to
		 * the redefinition of the frame creation methods.
		 * The analysis proceeds according to the BehaviourFrames logic, all we have to do here is
		 * to make sure that actual BehaviourFrames are returned and exploit their additional information.
		 */
		Frame<? extends AnValue>[] temp = analyzePrivately(owner, m, s);
		BehaviourFrame[] result = new BehaviourFrame[temp.length];
		IBehaviour b = null;
		for (int i = 0; i < temp.length; ++i) {
			result[i] = (BehaviourFrame) temp[i];
			if (result[i] == null)
				continue;
			if (result[i].frameBehaviour != null && b != null && result[i].frameBehaviour.equal(b))
				result[i].frameBehaviour = null;
			else if (result[i].frameBehaviour != null || b != null) {
				b = (result[i].frameBehaviour == null ? null : result[i].frameBehaviour.clone());
			}
		}
		
		List<String> deps = new ArrayList<String>();
		
		for (int i = 0; i < result.length; ++i)
			if (result[i] != null && result[i].getInvokedMethod() != null)
				deps.add(result[i].getInvokedMethod());
		
		context.signalDependancy(methodName, deps);

		return result;
	}
	
    protected BehaviourFrame newFrame(final int nLocals, final int nStack) {
		BehaviourFrame res = new BehaviourFrame(nLocals, nStack);
		res.addAnalysisInformations(methodName, context);
        return res;
    }

    protected BehaviourFrame newFrame(final Frame<? extends AnValue> src) {
    	BehaviourFrame r = new BehaviourFrame((BehaviourFrame)src);
        return r;
    }

}
