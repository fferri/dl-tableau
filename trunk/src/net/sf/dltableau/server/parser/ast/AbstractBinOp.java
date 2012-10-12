package net.sf.dltableau.server.parser.ast;

public abstract class AbstractBinOp extends AbstractNode {
	protected AbstractNode op1;
	
	protected AbstractNode op2;
	
	public AbstractNode getOp1() {
		return op1;
	}
	
	public AbstractNode getOp2() {
		return op2;
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof AbstractBinOp) {
			AbstractBinOp op = (AbstractBinOp)obj;
			return op.getClass().equals(getClass()) && op.op1.equals(op1) && op.op2.equals(op2);
		} else {
			return false;
		}
	}
}
