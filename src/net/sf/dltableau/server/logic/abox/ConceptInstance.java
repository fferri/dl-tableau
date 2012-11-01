package net.sf.dltableau.server.logic.abox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.*;

public class ConceptInstance extends AbstractInstance {
	protected final AbstractNode concept;
	protected final Individual individual;
	
	public ConceptInstance(AbstractNode concept, Individual individual) {
		this.concept = concept;
		this.individual = individual;
	}
	
	public AbstractNode getConcept() {
		return concept;
	}
	
	public boolean isAtomic() {
		return concept.isAtomic();
	}
	
	public ConceptInstance negatedInstance() {
		return new ConceptInstance(concept.negate(), individual);
	}
	
	public ConceptInstance removeParentheses() {
		AbstractNode tmp = concept;
		while(tmp instanceof Parens) tmp = ((Parens)tmp).getOp();
		return new ConceptInstance(tmp, individual);
	}
	
	public Individual getIndividual() {
		return individual;
	}
	
	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		return ExpressionRenderer.render(this, renderMode);
	}
	
	/**
	 * Project the individuals involved
	 * 
	 * @param l list of ConceptInstances
	 * @return list of individuals involved
	 */
	public static List<Individual> projectIndividuals(Collection<ConceptInstance> l) {
		List<Individual> r = new ArrayList<Individual>();
		for(ConceptInstance ci : l) {
			Individual i = ci.getIndividual();
			if(!r.contains(i)) r.add(i);
		}
		return r;
	}
	
	public static List<AbstractNode> projectConcepts(Collection<ConceptInstance> l) {
		List<AbstractNode> r = new ArrayList<AbstractNode>();
		for(ConceptInstance ci : l) {
			AbstractNode n = ci.getConcept();
			if(!r.contains(n)) r.add(n);
		}
		return r;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof ConceptInstance) {
			ConceptInstance i = (ConceptInstance)obj;
			return i.concept.equals(concept) && i.individual.equals(individual);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return concept.hashCode() + 17 * individual.hashCode();
	}
	
	public static boolean isSupersetOf(Collection<ConceptInstance> superSet, Collection<ConceptInstance> subSet) {
		List<AbstractNode> superSetC = projectConcepts(superSet);
		List<AbstractNode> subSetC = projectConcepts(subSet);
		for(AbstractNode i : subSetC)
			if(!superSetC.contains(i))
				return false;
		return true;
	}
}
