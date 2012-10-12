package net.sf.dltableau.server.logic.render;

import net.sf.dltableau.server.logic.tableau.AbstractInstance;
import net.sf.dltableau.server.logic.tableau.ConceptInstance;
import net.sf.dltableau.server.logic.tableau.RoleInstance;
import net.sf.dltableau.server.parser.ast.*;

public class ExpressionRenderer {
	public static String render(AbstractNode ast, boolean useUnicodeSymbols) {
		StringBuilder sb = new StringBuilder();
		render(ast, useUnicodeSymbols, sb);
		return sb.toString();
	}
	
	private static void render(AbstractNode ast, boolean useUnicodeSymbols, StringBuilder sb) {
		if(ast instanceof Parens) render((Parens)ast, useUnicodeSymbols, sb);
		else if(ast instanceof AbstractBinOp) render((AbstractBinOp)ast, useUnicodeSymbols, sb);
		else if(ast instanceof AbstractUnOp) render((AbstractUnOp)ast, useUnicodeSymbols, sb);
		else if(ast instanceof AbstractQuantifier) render((AbstractQuantifier)ast, useUnicodeSymbols, sb);
		else if(ast instanceof Atom) render((Atom)ast, useUnicodeSymbols, sb);
		else throw new IllegalArgumentException();
	}
	
	private static void render(AbstractBinOp n, boolean useUnicodeSymbols, StringBuilder sb) {
		render(n.getOp1(), useUnicodeSymbols, sb);
		sb.append(OperatorRenderer.render(n, useUnicodeSymbols));
		render(n.getOp2(), useUnicodeSymbols, sb);
	}
	
	private static void render(AbstractUnOp n, boolean useUnicodeSymbols, StringBuilder sb) {
		sb.append(OperatorRenderer.render(n, useUnicodeSymbols));
		render(n.getOp(), useUnicodeSymbols, sb);
	}
	
	private static void render(AbstractQuantifier n, boolean useUnicodeSymbols, StringBuilder sb) {
		sb.append(OperatorRenderer.render(n, useUnicodeSymbols));
		render(n.getRole(), useUnicodeSymbols, sb);
		sb.append(".");
		render(n.getExpression(), useUnicodeSymbols, sb);
	}
	
	private static void render(Parens n, boolean useUnicodeSymbols, StringBuilder sb) {
		sb.append("(");
		render(n.getOp(), useUnicodeSymbols, sb);
		sb.append(")");
	}
	
	private static void render(Atom n, boolean useUnicodeSymbols, StringBuilder sb) {
		sb.append(n.getName());
	}
	
	public static String render(AbstractInstance i, boolean useUnicodeSymbols) {
		if(i instanceof ConceptInstance) return render((ConceptInstance)i, useUnicodeSymbols);
		if(i instanceof RoleInstance) return render((RoleInstance)i, useUnicodeSymbols);
		throw new IllegalArgumentException();
	}
	
	public static String render(ConceptInstance i, boolean useUnicodeSymbols) {
		AbstractNode concept = i.getConcept();
		String conceptString = render(concept, useUnicodeSymbols);
		return (concept.isAtomic() ? (conceptString) : ("(" + conceptString + ")")) +
				"(" + getIndividualString(i.getIndividual()) + ")";
	}
	
	public static String render(RoleInstance i, boolean useUnicodeSymbols) {
		return render(i.getRole(), useUnicodeSymbols) + renderTuple(i, useUnicodeSymbols);
	}
	
	public static String renderTuple(RoleInstance i, boolean useUnicodeSymbols) {
		return "(" + getIndividualString(i.getIndividual1()) + "," + getIndividualString(i.getIndividual2()) + ")";
	}

	public static String getIndividualString(int i) {
		return String.format("%c", 'a' + i);
	}
}
