package net.sf.dltableau.server.logic.render;

import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.And;
import net.sf.dltableau.server.parser.ast.Exists;
import net.sf.dltableau.server.parser.ast.ForAll;
import net.sf.dltableau.server.parser.ast.Not;
import net.sf.dltableau.server.parser.ast.Or;
import net.sf.dltableau.server.parser.ast.Parens;
import net.sf.dltableau.server.parser.ast.SubsumedBy;

public class OperatorRenderer {
	public static String render(AbstractNode n, boolean useUnicodeSymbols) {
		if(n instanceof And) return useUnicodeSymbols ? " &#x2293; " : " and ";
		if(n instanceof Or) return useUnicodeSymbols ? " &#x2294; " : " or ";
		if(n instanceof Not) return useUnicodeSymbols ? "&not;" : "not ";
		if(n instanceof ForAll) return useUnicodeSymbols ? "&forall;" : "forall ";
		if(n instanceof Exists) return useUnicodeSymbols ? "&exist;" : "exists ";
		if(n instanceof SubsumedBy) return useUnicodeSymbols ? "&#x2291;" : " subsumed-by ";
		if(n instanceof Parens) return "( ... )";
		throw new IllegalArgumentException();
	}
}
