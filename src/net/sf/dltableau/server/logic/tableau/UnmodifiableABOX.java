package net.sf.dltableau.server.logic.tableau;

public class UnmodifiableABOX extends ABOX {
	// COPY constructor
	public UnmodifiableABOX(ABOX abox) {
		super(abox.parent);
		this.aList = abox.aList;
		this.children.addAll(abox.children);
		//this.parent = abox.parent;
		//this.level = abox.level;
		//this.childOrdinal = abox.childOrdinal;
		this.STRIP_PARENS = abox.STRIP_PARENS;
	}
	
	@Override
	public void add(AbstractInstance i) {
		throw new UnsupportedOperationException("Unmodifiable ABOX");
	}
}
