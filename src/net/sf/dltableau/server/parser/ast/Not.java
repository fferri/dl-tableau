package net.sf.dltableau.server.parser.ast;

public class Not extends AbstractUnOp {
	public Not(AbstractNode op) {
		this.op = op;
	}
	
	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}
}
