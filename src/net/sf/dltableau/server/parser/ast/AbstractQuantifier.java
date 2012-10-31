package net.sf.dltableau.server.parser.ast;

public abstract class AbstractQuantifier extends AbstractNode {
	protected final Atom role;
	protected final AbstractNode expression;
	
	public AbstractQuantifier(Atom role, AbstractNode expression) {
		this.role = role;
		this.expression = expression;
	}
	
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
	
	@Override
	public int hashCode() {
		int h = 0;
		h += role.hashCode() * 17;
		h += expression.hashCode() * 33;
		h += getClass().hashCode();
		return h;
	}
}
