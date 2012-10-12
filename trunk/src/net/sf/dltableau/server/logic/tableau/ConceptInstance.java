package net.sf.dltableau.server.logic.tableau;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.parser.ast.*;

public class ConceptInstance extends AbstractInstance {
	protected AbstractNode concept;
	protected int individual;
	
	public ConceptInstance(AbstractNode concept, int individual) {
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
		if(concept instanceof Not)
			return new ConceptInstance(((Not)concept).getOp(), individual);
		else
			return new ConceptInstance(new Not(concept), individual);
	}
	
	public int getIndividual() {
		return individual;
	}
	
	public String toString() {
		return ExpressionRenderer.render(this, false);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof ConceptInstance) {
			ConceptInstance i = (ConceptInstance)obj;
			return i.concept.equals(concept) && i.individual == individual;
		} else {
			return false;
		}
	}
}
