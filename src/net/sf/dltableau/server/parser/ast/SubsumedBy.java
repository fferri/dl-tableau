package net.sf.dltableau.server.parser.ast;

public class SubsumedBy extends AbstractBinOp {
	public SubsumedBy(AbstractNode op1, AbstractNode op2) {
		this.op1 = op1;
		this.op2 = op2;
	}

	@Override
	protected String opName() {
		return AbstractNode.SUBSUMED_BY;
	}
}
