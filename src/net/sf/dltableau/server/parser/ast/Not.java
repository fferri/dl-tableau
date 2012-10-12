package net.sf.dltableau.server.parser.ast;

public class Not extends AbstractUnOp {
	public Not(AbstractNode op) {
		super(op);
	}
	
	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}
}
