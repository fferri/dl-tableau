package net.sf.dltableau.server.logic.render;

import net.sf.dltableau.server.logic.tableau.AbstractInstance;
import net.sf.dltableau.server.logic.tableau.ConceptInstance;
import net.sf.dltableau.server.logic.tableau.RoleInstance;
import net.sf.dltableau.server.parser.ast.*;

public class ExpressionRenderer {
	public static String render(AbstractNode ast, RenderMode renderMode) {
		StringBuilder sb = new StringBuilder();
		render(ast, renderMode, sb);
		return sb.toString();
	}
	
	private static void render(AbstractNode ast, RenderMode renderMode, StringBuilder sb) {
		if(ast instanceof Parens) render((Parens)ast, renderMode, sb);
		else if(ast instanceof AbstractBinOp) render((AbstractBinOp)ast, renderMode, sb);
		else if(ast instanceof AbstractUnOp) render((AbstractUnOp)ast, renderMode, sb);
		else if(ast instanceof AbstractQuantifier) render((AbstractQuantifier)ast, renderMode, sb);
		else if(ast instanceof Atom) render((Atom)ast, renderMode, sb);
		else throw new IllegalArgumentException();
	}
	
	private static void render(AbstractBinOp n, RenderMode renderMode, StringBuilder sb) {
		render(n.getOp1(), renderMode, sb);
		sb.append(OperatorRenderer.render(n, renderMode));
		render(n.getOp2(), renderMode, sb);
	}
	
	private static void render(AbstractUnOp n, RenderMode renderMode, StringBuilder sb) {
		sb.append(OperatorRenderer.render(n, renderMode));
		render(n.getOp(), renderMode, sb);
	}
	
	private static void render(AbstractQuantifier n, RenderMode renderMode, StringBuilder sb) {
		sb.append(OperatorRenderer.render(n, renderMode));
		render(n.getRole(), renderMode, sb);
		sb.append(".");
		render(n.getExpression(), renderMode, sb);
	}
	
	private static void render(Parens n, RenderMode renderMode, StringBuilder sb) {
		sb.append("(");
		render(n.getOp(), renderMode, sb);
		sb.append(")");
	}
	
	private static void render(Atom n, RenderMode renderMode, StringBuilder sb) {
		sb.append(n.getName());
	}
	
	public static String render(AbstractInstance i, RenderMode renderMode) {
		if(i instanceof ConceptInstance) return render((ConceptInstance)i, renderMode);
		if(i instanceof RoleInstance) return render((RoleInstance)i, renderMode);
		throw new IllegalArgumentException();
	}
	
	public static String render(ConceptInstance i, RenderMode renderMode) {
		AbstractNode concept = i.getConcept();
		String conceptString = render(concept, renderMode);
		return (concept.isAtomic() ? (conceptString) : ("(" + conceptString + ")")) +
				"(" + IndividualRenderer.render(i.getIndividual(), renderMode) + ")";
	}
	
	public static String render(RoleInstance i, RenderMode renderMode) {
		return render(i.getRole(), renderMode) + renderTuple(i, renderMode);
	}
	
	public static String renderTuple(RoleInstance i, RenderMode renderMode) {
		return "(" + IndividualRenderer.render(i.getIndividual1(), renderMode) + ","
				+ IndividualRenderer.render(i.getIndividual2(), renderMode) + ")";
	}
}
