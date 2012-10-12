package net.sf.dltableau.server.logic.tableau;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.*;

public class ConceptInstance extends AbstractInstance {
	protected final AbstractNode concept;
	protected final int individual;
	
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
		return new ConceptInstance(concept.negate(), individual);
	}
	
	public int getIndividual() {
		return individual;
	}
	
	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		return ExpressionRenderer.render(this, renderMode);
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
