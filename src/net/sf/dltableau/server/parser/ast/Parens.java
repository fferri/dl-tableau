package net.sf.dltableau.server.parser.ast;

public class Parens extends AbstractUnOp {
	public Parens(AbstractNode expression) {
		super(expression);
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
}
