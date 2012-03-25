package net.sf.dltableau.server.parser.ast;

public class Not extends AbstractUnOp {
	public Not(AbstractNode op) {
		this.op = op;
	}

	@Override
	protected String opName() {
		return AbstractNode.NOT;
	}
	
	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}
}
