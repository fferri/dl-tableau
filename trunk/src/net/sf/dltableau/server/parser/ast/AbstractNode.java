package net.sf.dltableau.server.parser.ast;

public abstract class AbstractNode {
	public final String treeString() {
		StringBuilder b = new StringBuilder();
		treeString(0, b);
		return b.toString();
	}
	
	protected abstract void treeString(int level, StringBuilder builder);
	
	protected static String indent(int level) {
		if(level <= 0) return "";
		else return "  " + indent(--level);
	}
	
	public abstract boolean isAtomic();
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
