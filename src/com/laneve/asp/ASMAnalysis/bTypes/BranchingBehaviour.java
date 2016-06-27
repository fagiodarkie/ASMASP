package com.laneve.asp.ASMAnalysis.bTypes;

import java.util.List;

/**
 * This class captures the behaviours of branching instructions: IFs,
 * but also those instructions which produce 1, 0 or -1 depending on the actual value
 * of some variable.
 * 
 * Jumps may also be unconditioned. this is the case for expressions which are not in Presburgher or
 * in class comparison / exceptions / other things our analysis does not cover yet.
 * 
 * The behaviours given as parameters must share the same caption and describe the successive states
 * in each of the provided conditions (if any).
 * 
 * LCMP
 * B_i(x1:int, x2:int, x3:int) = [x3 < 0]B_j(x1, x2, -1) + [x3 == 0]B_j(x1, x2, 0) + [x3 > 0]B_j(x1, x2, 1)
 * 
 * IDIV ; IFGT (notice that x3 has unknown type)
 * B_i(x1:int, x2:int, x3:-) = B_i+1(x1, x2) + B_j(x1, x2)
 * 
 * @author jacopo.freddi
 *
 */
public class BranchingBehaviour implements IBehaviour {

	protected List<SimpleBehaviour> list;
	protected List<String> conditions;
	
	public BranchingBehaviour(List<SimpleBehaviour> behaviours,
			List<String> conditions) {
		list = behaviours;
		this.conditions = conditions;
	}
	
	@Override
	public String printBehaviour() {
		String res = list.get(0).getCaption() + " = ";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0)
				res += " + ";
			res += (conditions == null ? "" : "[" + conditions.get(i) + "]")
					+ list.get(i).getBody();
		}
		
		return res;
	}

}
