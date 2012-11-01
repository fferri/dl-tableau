package net.sf.dltableau.server.logic.abox;

import java.util.Set;

import net.sf.dltableau.server.logic.render.IndividualRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;

public class Individual {
	private final int ordinal;
	
	private Individual(int ordinal) {
		this.ordinal = ordinal;
	}
	
	public int ordinal() {
		return ordinal;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Individual) {
			return ((Individual)obj).ordinal == ordinal;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new Integer(ordinal).hashCode();
	}
	
	@Override
	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		return IndividualRenderer.render(this, renderMode);
	}
	
	public static Individual newIndividual(ABOX abox) {
		Set<Individual> is = abox.getIndividuals(true);
		int max = -1;
		for(Individual i : is) if(i.ordinal > max) max = i.ordinal;
		return new Individual(max + 1);
	}
}
