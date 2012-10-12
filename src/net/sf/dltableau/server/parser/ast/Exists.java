package net.sf.dltableau.server.parser.ast;

public class Exists extends AbstractQuantifier {
	public Exists(Atom role, AbstractNode expression) {
		this.role = role;
		this.expression = expression;
	}
}
