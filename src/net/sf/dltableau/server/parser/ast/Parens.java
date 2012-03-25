package net.sf.dltableau.server.parser.ast;

public class Parens extends AbstractUnOp {
	public Parens(AbstractNode expression) {
		this.op = expression;
	}
	
	public String toString() {
		return "(" + op + ")";
	}

	@Override
	protected String opName() {
		return "(...)";
	}
	
	@Override
	protected void treeString(int level, StringBuilder builder) {
		op.treeString(level, builder);
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
}
