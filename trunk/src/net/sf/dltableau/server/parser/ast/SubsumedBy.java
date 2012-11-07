package net.sf.dltableau.server.parser.ast;

public class SubsumedBy extends AbstractDefinition {
	public SubsumedBy(AbstractNode op1, AbstractNode op2) {
		super(op1, op2);
	}
	
	@Override
	public Or asNormalForm() {
		return new Or(new Not(op1), op2);
	}
}
