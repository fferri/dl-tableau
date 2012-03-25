package net.sf.dltableau.server.parser.ast;

public abstract class AbstractUnOp extends AbstractNode {
	protected AbstractNode op;
	
	public AbstractNode getOp() {
		return op;
	}
	
	public String toString() {
		return opName() + op;
	}
	
	protected abstract String opName();
	
	@Override
	protected void treeString(int level, StringBuilder builder) {
		builder.append(indent(level) + opName() + "\n");
		level++;
		op.treeString(level, builder);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof AbstractUnOp) {
			AbstractUnOp n = (AbstractUnOp)obj;
			return n.opName().equals(opName()) && n.op.equals(op);
		} else {
			return false;
		}
	}
}
