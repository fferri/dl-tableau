package net.sf.dltableau.server.parser.ast;

public abstract class AbstractNode {
	public static String AND = "and";
	public static String OR = "or";
	public static String NOT = "not";
	public static String FORALL = "forall";
	public static String EXISTS = "exists";
	public static String SUBSUMED_BY = "subsumed-by";
	
	public static void setUnicodeRendering(boolean useUnicode) {
		AbstractNode.AND = (useUnicode) ? "&#x2293;" : " and ";
		AbstractNode.OR = (useUnicode) ? "&#x2294;" : " or ";
		AbstractNode.NOT = (useUnicode) ? "&not;" : " not ";
		AbstractNode.FORALL = (useUnicode) ? "&forall;" : " forall ";
		AbstractNode.EXISTS = (useUnicode) ? "&exist;" : " exists ";
		AbstractNode.SUBSUMED_BY = (useUnicode) ? "&#x2291;" : " subsumed-by ";
	}
	
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
