package net.sf.dltableau.server.logic.tableau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dltableau.server.logic.LogicUtils;
import net.sf.dltableau.server.logic.abox.ABOX;
import net.sf.dltableau.server.logic.abox.AbstractInstance;
import net.sf.dltableau.server.logic.abox.ConceptInstance;
import net.sf.dltableau.server.logic.abox.Individual;
import net.sf.dltableau.server.logic.abox.RoleInstance;
import net.sf.dltableau.server.logic.abox.UnmodifiableABOX;
import net.sf.dltableau.server.logic.tbox.TBOX;
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
public class Tableau {
	private TBOX tbox = null;
	private ABOX abox0 = null;
		
	public void init(AbstractNode concept) {
		init(new TBOX(), concept);
	}
	
	public void init(TBOX tbox, AbstractNode concept) {
		this.abox0 = new ABOX(null);
		this.tbox = tbox;
		
		concept = LogicUtils.toNegationNormalForm(concept);
		
		Individual x0 = abox0.getNewIndividual();
		this.abox0.add(new ConceptInstance(concept, x0));
		addTBOXAxioms(x0, abox0);
	}
	
	public TBOX getTBOX() {
		return tbox;
	}
	
	public ABOX getABOX() {
		return new UnmodifiableABOX(abox0);
	}
	
	private void addTBOXAxioms(Individual i, ABOX abox) {
		if(tbox == null) return;
		
		for(Or x : tbox.getNormalFormAxioms())
			abox.add(new ConceptInstance(x, i));
	}
	
	public List<ABOX> getAllBranches() {
		List<ABOX> leaves = new ArrayList<ABOX>();
		getAllBranches(abox0, leaves);
		return leaves;
	}
	
	private static void getAllBranches(ABOX abox, List<ABOX> leaves) {
		if(abox.isLeaf()) leaves.add(abox);
		else for(ABOX ab : abox.getChildren()) getAllBranches(ab, leaves);
	}
	
	public List<ABOX> getOpenBranches() {
		List<ABOX> allBranches = getAllBranches();
		List<ABOX> openBranches = new ArrayList<ABOX>();
		for(ABOX ab : allBranches)
			if(!ab.containsClash())
				openBranches.add(ab);
		return openBranches;
	}
	
	public ABOX getABOXById(long id) {
		return getABOXById(abox0, id);
	}
	
	protected ABOX getABOXById(ABOX root, long id) {
		if(root.getId() == id)
			return root;
		ABOX r;
		for(ABOX child : root.getChildren()) {
			r = getABOXById(child, id);
			if(r != null) return r;
		}
		return null;
	}
	
	/**
	 * Check if the tableau is closed, that is, every branch
	 * contains a clash.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		//return getOpenBranches().isEmpty();
		
		// faster:
		List<ABOX> allBranches = getAllBranches();
		for(ABOX ab : allBranches)
			if(!ab.containsClash())
				return false;
		return true;
	}
	
	/**
	 * Return a map of available expansions for every open branch
	 * of the tableau.
	 * 
	 * @return
	 */
	public Map<ABOX, List<ConceptInstance>> getAvailableExpansions() {
		Map<ABOX, List<ConceptInstance>> ret = new HashMap<ABOX, List<ConceptInstance>>();
		for(ABOX branch : getOpenBranches())
			ret.put(branch, getAvailableExpansions(branch));
		return ret;
	}
	
	/**
	 * Return a list of available expansions for the given branch
	 * of the tableau.
	 * 
	 * @param abox the given branch
	 * @return
	 */
	public List<ConceptInstance> getAvailableExpansions(ABOX abox) {
		List<ConceptInstance> ret = new ArrayList<ConceptInstance>();
		if(abox.containsClash()) return ret;
		for(int j = 0; j < abox.size(); j++) {
			AbstractInstance i = abox.get(j);
			if(!(i instanceof ConceptInstance)) continue;
			if(expandStep((ConceptInstance)i, abox, true))
				ret.add((ConceptInstance)i);
		}
		return ret;
	}
	
	/**
	 * Perform expandStep() until possible.
	 */
	public void expand() {
		while(expandStep());
	}
	
	/**
	 * Do one expansion step in the first available branch
	 * 
	 * @return true if one expansion has been done.
	 *         false if no possible expansion exists.
	 */
	public boolean expandStep() {
		for(ABOX abox1 : getAllBranches())
			if(expandStep(abox1))
				return true;
		return false;
	}
	
	/**
	 * Do one expansion step in the specified branch
	 * 
	 * @param abox the specified branch
	 * @return true if one expansion has been done.
	 *         false if no possible expansion exists.
	 */
	public boolean expandStep(ABOX abox) {
		// can't expand a closed branch:
		if(abox.containsClash())
			return false;
		
		// expansion carries only at leaf branches
		if(!abox.isLeaf())
			return false;
		
		for(int j = 0; j < abox.size(); j++) {
			AbstractInstance i = abox.get(j);
			if(!(i instanceof ConceptInstance)) continue;
			if(expandStep((ConceptInstance)i, abox, false)) return true;
		}
		return false;
	}
	
	/**
	 * Expand the given ConceptInstance in the specified ABOX
	 *
	 * @param ci
	 * @param abox
	 * @param pretend
	 * @return
	 */
	public boolean expandStep(ConceptInstance ci, ABOX abox) {
		return expandStep(ci, abox, false);
	}
	
	/**
	 * Check whether the given ConceptInstance can be expanded in the specified ABOX
	 * 
	 * @param ci
	 * @param abox
	 * @return
	 */
	public boolean canExpandStep(ConceptInstance ci, ABOX abox) {
		return expandStep(ci, abox, true);
	}
	
	protected boolean expandStep(ConceptInstance ci, ABOX abox, boolean pretend) {
		if(!abox.contains(ci))
			throw new IllegalArgumentException("ABOX " + abox.getName() + " does not contain such instance: " + ci);
		
		// can't expand a closed branch:
		if(abox.containsClash())
			return false;
		
		// expansion carries only at leaf branches
		if(!abox.isLeaf())
			return false;
		
		AbstractNode c = ci.getConcept();
		Individual x1 = ci.getIndividual();
		
		if(c instanceof And) {
			if(expandStep((And)c, x1, abox, pretend)) return true;
		} else if(c instanceof Or) {
			if(expandStep((Or)c, x1, abox, pretend)) return true;				
		} else if(c instanceof Exists) {
			if(expandStep((Exists)c, ci.getIndividual(), abox, pretend)) return true;
		} else if(c instanceof ForAll) {
			if(expandStep((ForAll)c, x1, abox, pretend)) return true;
		} else if(c instanceof Parens) {
			if(expandStep((Parens)c, x1, abox, pretend)) return true;
		}
		
		return false;
	}
	
	protected boolean expandStep(And and, Individual x1, ABOX abox, boolean pretend) {
		ConceptInstance ci1 = new ConceptInstance(and.getOp1(), x1);
		ConceptInstance ci2 = new ConceptInstance(and.getOp2(), x1);

		if(!(abox.contains(ci1) && abox.contains(ci2))) {
			if(!pretend) {
				ABOX a1 = new ABOX(abox);
				a1.add(ci1);
				a1.add(ci2);
			}
			return true;
		}
		
		return false;
	}
	
	protected boolean expandStep(Or or, Individual x1, ABOX abox, boolean pretend) {
		ConceptInstance ci1 = new ConceptInstance(or.getOp1(), x1);
		ConceptInstance ci2 = new ConceptInstance(or.getOp2(), x1);

		if(!(abox.contains(ci1) || abox.contains(ci2))) {
			if(!pretend) {
				ABOX a1 = new ABOX(abox), a2 = new ABOX(abox);
				a1.add(ci1);
				a2.add(ci2);
			}
			return true;
		}
		
		return false;
	}
	
	protected boolean expandStep(ForAll forall, Individual x1, ABOX abox, boolean pretend) {
		// selects x2 in R(x1,x2):
		List<Individual> l = abox.getMatchingIndividualsByRole(forall.getRole(), x1);
		
		for(Individual x2 : l) {
			ConceptInstance ci1 = new ConceptInstance(forall.getExpression(), x2);
			if(!abox.contains(ci1)) {
				if(!pretend) {
					ABOX a1 = new ABOX(abox);
					a1.add(ci1);
				}
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean expandStep(Exists exists, Individual x1, ABOX abox, boolean pretend) {
		if(blocked(x1, abox)) return false;
		// selects x2 in R(x1,x2):
		List<Individual> l = abox.getMatchingIndividualsByRole(exists.getRole(), x1);
		
		// check if exists C(x2), if yes, stop here
		for(Individual x2 : l)
			if(abox.contains(new ConceptInstance(exists.getExpression(), x2)))
				return false;
		
		if(!pretend) {
			Individual newIndividual = abox.getNewIndividual();
			ConceptInstance ci1 = new ConceptInstance(exists.getExpression(), newIndividual);
			RoleInstance ri1 = new RoleInstance(exists.getRole(), x1, newIndividual);
			ABOX a1 = new ABOX(abox);
			a1.add(ci1);
			a1.add(ri1);
			addTBOXAxioms(newIndividual, a1);
		}
		return true;
	}
	
	protected boolean expandStep(Parens parens, Individual x1, ABOX abox, boolean pretend) {
		ConceptInstance ci1 = new ConceptInstance(parens.getOp(), x1);
		
		if(!abox.contains(ci1)) {
			if(!pretend) {
				abox.add(ci1);
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check BLOCKING condition (i.e. find a superset of concept instances in i
	 * in the ancestors of i.
	 * @param i
	 * @return
	 */
	protected boolean blocked(Individual i, ABOX abox) {
		List<ConceptInstance> cl = abox.getConceptInstancesByIndividual(i, true);
		List<Individual> ancestors = abox.getAncestors(i);
		for(Individual ancestor : ancestors) {
			List<ConceptInstance> acl = abox.getConceptInstancesByIndividual(ancestor, true);
			if(ConceptInstance.isSupersetOf(acl, cl))
				return true;
		}
		return false;
	}
}
