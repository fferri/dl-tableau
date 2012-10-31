package net.sf.dltableau.server.logic.abox;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.Atom;

public class RoleInstance extends AbstractInstance {
	protected final Atom role;
	protected final int individual1;
	protected final int individual2;
	
	public RoleInstance(Atom role, int i1, int i2) {
		this.role = role;
		this.individual1 = i1;
		this.individual2 = i2;
	}
	
	public int getIndividual1() {
		return individual1;
	}
	
	public int getIndividual2() {
		return individual2;
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj.getClass().equals(RoleInstance.class)) {
			RoleInstance i = (RoleInstance)obj;
			return i.role.equals(role) && i.individual1 == individual1 && i.individual2 == individual2;
		} else {
			return false;
		}
	}
}
