package net.sf.dltableau.server.parser.ast;

public class ForAll extends AbstractQuantifier {
	public ForAll(Atom role, AbstractNode expression) {
		this.role = role;
		this.expression = expression;
	}

	@Override
	protected String quantifierName() {
		return SyntaxRenderer.FORALL();
	}
}
