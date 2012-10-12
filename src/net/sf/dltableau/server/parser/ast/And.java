package net.sf.dltableau.server.parser.ast;

public class And extends AbstractBinOp {
	public And(AbstractNode op1, AbstractNode op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
}
