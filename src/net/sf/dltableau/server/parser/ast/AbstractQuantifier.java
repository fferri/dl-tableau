package net.sf.dltableau.server.parser.ast;

public abstract class AbstractQuantifier extends AbstractNode {
	protected Atom role;
	
	protected AbstractNode expression;
	
	public Atom getRole() {
		return role;
	}
	
	public AbstractNode getExpression() {
		return expression;
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof AbstractQuantifier) {
			AbstractQuantifier q = (AbstractQuantifier)obj;
			return q.getClass().equals(getClass()) && q.role.equals(role) && q.expression.equals(expression);
		} else {
			return false;
		}
	}
}
