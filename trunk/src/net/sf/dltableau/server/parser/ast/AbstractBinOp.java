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
	
	public String toString() {
		return op1 + opName() + op2;
	}
	
	protected abstract String opName();
	
	@Override
	protected void treeString(int level, StringBuilder builder) {
		builder.append(indent(level) + opName() + "\n");
		level++;
		op1.treeString(level, builder);
		op2.treeString(level, builder);
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof AbstractBinOp) {
			AbstractBinOp op = (AbstractBinOp)obj;
			return op.opName().equals(opName()) && op.op1.equals(op1) && op.op2.equals(op2);
		} else {
			return false;
		}
	}
}
