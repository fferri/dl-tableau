package net.sf.dltableau.server.parser.ast;

public class Parens extends AbstractUnOp {
	public Parens(AbstractNode expression) {
		this.op = expression;
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
}
