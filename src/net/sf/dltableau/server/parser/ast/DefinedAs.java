package net.sf.dltableau.server.parser.ast;

public class DefinedAs extends AbstractDefinition {
	public DefinedAs(Atom op1, AbstractNode op2) {
		super(op1, op2);
	}
	
	public Atom getConceptName() {
		return (Atom)op1;
	}
	
	@Override
	public Or asNormalForm() {
		return new Or(new And(op1, op2), new And(new Not(op1), new Not(op2)));
	}
}
