package net.sf.dltableau.server.logic.abox;

import java.util.List;

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
		List<ConceptInstance> lc = abox.getConceptInstances();
		List<RoleInstance> lr = abox.getRoleInstances();
		List<Individual> i1 = ConceptInstance.projectIndividuals(lc),
				i2 = RoleInstance.projectIndividuals(lr, RoleInstance.Side.S1),
				i3 = RoleInstance.projectIndividuals(lr, RoleInstance.Side.S2);
		int max = -1;
		for(Individual i : i1) if(i.ordinal > max) max = i.ordinal;
		for(Individual i : i2) if(i.ordinal > max) max = i.ordinal;
		for(Individual i : i3) if(i.ordinal > max) max = i.ordinal;
		return new Individual(max + 1);
	}
}
