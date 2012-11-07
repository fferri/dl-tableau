package net.sf.dltableau.server.parser.ast;

public abstract class AbstractDefinition extends AbstractBinOp {
	public AbstractDefinition(AbstractNode op1, AbstractNode op2) {
		super(op1, op2);
	}
	
	public abstract Or asNormalForm();
}
