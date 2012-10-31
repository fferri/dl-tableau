package net.sf.dltableau.server.parser.ast;

public class AbstractDefinition extends AbstractBinOp {
	public AbstractDefinition(Atom op1, AbstractNode op2) {
		super(op1, op2);
	}
	
	public Atom getConcept() {
		return (Atom)op1;
	}
	
	public AbstractNode getDefinition() {
		return op2;
	}
}
