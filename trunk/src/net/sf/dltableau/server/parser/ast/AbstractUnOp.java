package net.sf.dltableau.server.parser.ast;

public abstract class AbstractUnOp extends AbstractNode {
	protected AbstractNode op;
	
	public AbstractUnOp(AbstractNode op) {
		this.op = op;
	}
	
	public AbstractNode getOp() {
		return op;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof AbstractUnOp) {
			AbstractUnOp n = (AbstractUnOp)obj;
			return n.getClass().equals(getClass()) && n.op.equals(op);
		} else {
			return false;
		}
	}
}
