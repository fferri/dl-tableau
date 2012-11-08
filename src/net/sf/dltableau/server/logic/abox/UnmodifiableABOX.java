package net.sf.dltableau.server.logic.abox;

import java.util.ArrayList;
import java.util.List;

public class UnmodifiableABOX extends ABOX {
	// COPY constructor
	public UnmodifiableABOX(ABOX abox) {
		super(abox.parent, abox.id);
		
		this.aList.addAll(abox.aList);
		this.aListConcepts.addAll(abox.aListConcepts);
		this.aListRoles.addAll(abox.aListRoles);
		this.aSetIndividuals.addAll(abox.aSetIndividuals);

		this.children.addAll(abox.children);
	}
	
	@Override
	public void add(AbstractInstance i) {
		throw new UnsupportedOperationException("Unmodifiable ABOX");
	}
	
	@Override
	public ABOX getParent() {
		return new UnmodifiableABOX(super.getParent());
	}
	
	@Override
	public List<ABOX> getChildren() {
		List<ABOX> r = new ArrayList<ABOX>();
		for(ABOX child : children) r.add(new UnmodifiableABOX(child));
		return r;
	}
}
