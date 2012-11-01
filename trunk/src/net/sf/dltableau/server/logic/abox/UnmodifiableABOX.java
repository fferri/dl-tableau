package net.sf.dltableau.server.logic.abox;

public class UnmodifiableABOX extends ABOX {
	// COPY constructor
	public UnmodifiableABOX(ABOX abox) {
		super(abox.parent);
		this.aList.addAll(abox.aList);
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
}
