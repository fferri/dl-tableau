package net.sf.dltableau.server.logic.abox;

import java.util.ArrayList;
import java.util.List;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.Atom;

public class RoleInstance extends AbstractInstance {
	public static enum Side {S1, S2};
	
	protected final Atom role;
	protected final Individual individual1;
	protected final Individual individual2;
	
	public RoleInstance(Atom role, Individual i1, Individual i2) {
		this.role = role;
		this.individual1 = i1;
		this.individual2 = i2;
	}
	
	public Individual getIndividual1() {
		return individual1;
	}
	
	public Individual getIndividual2() {
		return individual2;
	}
	
	public Individual getIndividual(Side s) {
		if(s.equals(Side.S1)) return individual1;
		if(s.equals(Side.S2)) return individual2;
		return null;
	}
	
	public Atom getRole() {
		return role;
	}

	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		return ExpressionRenderer.render(this, renderMode);
	}
	
	/**
	 * Select roles matching the given arguments.
	 * null means any (wildcard).
	 * 
	 * @param l list of RoleInstances
	 * @param i1 left participant to the role
	 * @param i2 right participant to the role 
	 * @return list of matching RoleInstances
	 */
	public static List<RoleInstance> selectRoleInstances(List<RoleInstance> l, Individual i1, Individual i2) {
		List<RoleInstance> r = new ArrayList<RoleInstance>();
		for(RoleInstance ri : l) {
			if(r.contains(ri)) continue;
			if(i1 != null && !i1.equals(ri.getIndividual1())) continue;
			if(i2 != null && !i2.equals(ri.getIndividual2())) continue;
			r.add(ri);
		}
		return r;
	}
	
	/**
	 * Project the individuals involved in the specified side of the relation
	 * 
	 * @param l list of RoleInstances
	 * @param s side of the relation
	 * @return list of individuals involved
	 */
	public static List<Individual> projectIndividuals(List<RoleInstance> l, Side s) {
		List<Individual> r = new ArrayList<Individual>();
		for(RoleInstance ri : l) {
			Individual i = ri.getIndividual(s);
			if(!r.contains(i)) r.add(i);
		}
		return r;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj.getClass().equals(RoleInstance.class)) {
			RoleInstance i = (RoleInstance)obj;
			return i.role.equals(role) && i.individual1 == individual1 && i.individual2 == individual2;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return role.hashCode() + 23 * individual1.hashCode() + 17 * individual2.hashCode();
	}
}
