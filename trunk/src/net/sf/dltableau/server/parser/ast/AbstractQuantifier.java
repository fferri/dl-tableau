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
	
	public String toString() {
		return quantifierName() + role + "." + expression;
	}
	
	protected abstract String quantifierName();
	
	@Override
	protected void treeString(int level, StringBuilder builder) {
		builder.append(indent(level) + quantifierName() + "\n");
		level++;
		role.treeString(level, builder);
		expression.treeString(level, builder);
	}
	
	@Override
	public boolean isAtomic() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof AbstractQuantifier) {
			AbstractQuantifier q = (AbstractQuantifier)obj;
			return q.quantifierName().equals(quantifierName()) && q.role.equals(role) && q.expression.equals(expression);
		} else {
			return false;
		}
	}
}
