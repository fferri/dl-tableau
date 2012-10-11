package net.sf.dltableau.server.logic.tableau;

import java.util.ArrayList;
import java.util.List;

import net.sf.dltableau.server.parser.ast.*;

/**
 * Model of tableau, containing a tree of ABOXes.
 * Each ABOX contains the instances in the current branch,
 * and implicitly contains all the instances in its
 * ancestors, recursively.
 * 
 * Tableau performs the expansion (according to the well known rules).
 * Each expansion step adds one or two ABOXes as child
 * of the current ABOX.
 * 
 * @author Federico Ferri
 *
 */
public class Tableau implements Cloneable {
	private ABOX abox0 = null;
		
	public void init(AbstractNode concept) {
		abox0 = new ABOX(null);
		abox0.add(new ConceptInstance(concept, abox0.getNewIndividual()));
	}
	
	public ABOX abox() {
		return abox0;
	}
	
	public List<ABOX> allBranches() {
		List<ABOX> leaves = new ArrayList<ABOX>();
		allBranches(abox0, leaves);
		return leaves;
	}
	
	private static void allBranches(ABOX abox, List<ABOX> leaves) {
		if(abox.children().isEmpty()) leaves.add(abox);
		else for(ABOX ab : abox.children()) allBranches(ab, leaves);
	}
	
	public List<ABOX> openBranches() {
		List<ABOX> allBranches = allBranches();
		List<ABOX> openBranches = new ArrayList<ABOX>();
		for(ABOX ab : allBranches)
			if(!ab.containsClash())
				openBranches.add(ab);
		return openBranches;
	}
	
	public boolean isClosed() {
		return openBranches().isEmpty();
	}
	
	public boolean expandStep() {
		return expandStepR(abox0);
	}
	
	protected boolean expandStepR(ABOX abox) {
		if(abox.isLeaf())
			return expandStep(abox);
		for(ABOX abox1 : abox.children()) {
			boolean r = expandStepR(abox1);
			if(r) return true;
		}
		return false;
	}
	
	protected boolean expandStep(ABOX abox) {
		if(abox.containsClash())
			return false;
		
		for(int j = 0; j < abox.size(); j++) {
			AbstractInstance i = abox.get(j);
			if(!(i instanceof ConceptInstance)) continue;
			ConceptInstance ci = (ConceptInstance)i;
			AbstractNode c = ci.getConcept();
			int x1 = ci.getIndividual();
			
			if(c instanceof And) {
				if(expandStep((And)c, x1, abox)) return true;
			} else if(c instanceof Or) {
				if(expandStep((Or)c, x1, abox)) return true;				
			} else if(c instanceof Exists) {
				if(expandStep((Exists)c, ci.getIndividual(), abox)) return true;
			} else if(c instanceof ForAll) {
				if(expandStep((ForAll)c, x1, abox)) return true;
			}
		}
		return false;
	}
	
	protected boolean expandStep(And and, int x1, ABOX abox) {
		ConceptInstance ci1 = new ConceptInstance(and.getOp1(), x1);
		ConceptInstance ci2 = new ConceptInstance(and.getOp2(), x1);

		if(!(abox.contains(ci1) && abox.contains(ci2))) {
			ABOX a1 = new ABOX(abox);
			a1.add(ci1);
			a1.add(ci2);
			return true;
		}
		
		return false;
	}
	
	protected boolean expandStep(Or or, int x1, ABOX abox) {
		ConceptInstance ci1 = new ConceptInstance(or.getOp1(), x1);
		ConceptInstance ci2 = new ConceptInstance(or.getOp2(), x1);

		if(!(abox.contains(ci1) || abox.contains(ci2))) {
			ABOX a1 = new ABOX(abox), a2 = new ABOX(abox);
			a1.add(ci1);
			a2.add(ci2);
			return true;
		}
		
		return false;
	}
	
	protected boolean expandStep(ForAll forall, int x1, ABOX abox) {
		List<Integer> l = abox.getMatchingIndividualsByRole(forall.getRole(), x1);
		for(Integer x2 : l) {
			ConceptInstance ci1 = new ConceptInstance(forall.getExpression(), x2);
			if(!abox.contains(ci1)) {
				ABOX a1 = new ABOX(abox);
				a1.add(ci1);
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean expandStep(Exists exists, int x1, ABOX abox) {
		boolean foundPCT = false;
		List<Integer> l = abox.getMatchingIndividualsByRole(exists.getRole(), x1);
		for(Integer x2 : l) {
			if(abox.contains(new ConceptInstance(exists.getExpression(), x2))) {
				foundPCT = true;
				break;
			}
		}
		
		if(!foundPCT) {
			int newIndividual = abox.getNewIndividual();
			ConceptInstance ci1 = new ConceptInstance(exists.getExpression(), newIndividual);
			RoleInstance ri1 = new RoleInstance(exists.getRole(), x1, newIndividual);
			ABOX a1 = new ABOX(abox);
			a1.add(ci1);
			a1.add(ri1);
			return true;
		}
		
		return false;
	}
	
	protected boolean expandStep(Parens parens, int x1, ABOX abox) {
		ConceptInstance ci1 = new ConceptInstance(parens.getOp(), x1);
		
		if(!abox.contains(ci1)) {
			abox.add(ci1);
			return true;
		}
		
		return false;
	}
	
	public void expand() {
		while(expandStep());
	}
}
