package net.sf.dltableau.server.logic.render;

import net.sf.dltableau.server.parser.ast.AbstractBinOp;
import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.AbstractQuantifier;
import net.sf.dltableau.server.parser.ast.AbstractUnOp;
import net.sf.dltableau.server.parser.ast.Atom;

public class ASTRenderer {
	public static String render(AbstractNode ast, boolean useUnicodeSymbols, boolean HTML) {
		StringBuilder sb = new StringBuilder();
		render(ast, useUnicodeSymbols, HTML, sb, 0);
		return sb.toString();
	}
	
	private static void render(AbstractNode n, boolean useUnicodeSymbols, boolean HTML, StringBuilder sb, int level) {
		if(n instanceof AbstractBinOp) render((AbstractBinOp)n, useUnicodeSymbols, HTML, sb, level);
		else if(n instanceof AbstractUnOp) render((AbstractUnOp)n, useUnicodeSymbols, HTML, sb, level);
		else if(n instanceof AbstractQuantifier) render((AbstractQuantifier)n, useUnicodeSymbols, HTML, sb, level);
		else if(n instanceof Atom) render((Atom)n, useUnicodeSymbols, HTML, sb, level);
		else throw new IllegalArgumentException();
	}
	
	private static void render(AbstractBinOp n, boolean useUnicodeSymbols, boolean HTML, StringBuilder sb, int level) {
		if(HTML) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(OperatorRenderer.render(n, useUnicodeSymbols));
		
		if(HTML) sb.append("</td><td></td></tr><tr><td></td><td style=\"border-left: 1px solid black;\">");
		else sb.append("\n");
		
		render(n.getOp1(), useUnicodeSymbols, HTML, sb, level + 1);
		render(n.getOp2(), useUnicodeSymbols, HTML, sb, level + 1);
		
		if(HTML) sb.append("</td></tr></table>");
	}

	private static void render(AbstractUnOp n, boolean useUnicodeSymbols, boolean HTML, StringBuilder sb, int level) {
		if(HTML) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(OperatorRenderer.render(n, useUnicodeSymbols));
		
		if(HTML) sb.append("</td><td></td></tr><tr><td></td><td style=\"border-left: 1px solid black;\">");
		else sb.append("\n");
		
		render(n.getOp(), useUnicodeSymbols, HTML, sb, level + 1);
		
		if(HTML) sb.append("</td></tr></table>");
	}

	private static void render(AbstractQuantifier n, boolean useUnicodeSymbols, boolean HTML, StringBuilder sb, int level) {
		if(HTML) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(OperatorRenderer.render(n, useUnicodeSymbols));
		
		if(HTML) sb.append("</td><td></td></tr><tr><td></td><td style=\"border-left: 1px solid black;\">");
		else sb.append("\n");
		
		render(n.getRole(), useUnicodeSymbols, HTML, sb, level + 1);
		render(n.getExpression(), useUnicodeSymbols, HTML, sb, level + 1);
		
		if(HTML) sb.append("</td></tr></table>");
	}

	private static void render(Atom n, boolean useUnicodeSymbols, boolean HTML, StringBuilder sb, int level) {
		if(HTML) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(n.getName());
		
		if(HTML) sb.append("</td></tr></table>");
		else sb.append("\n");
	}
	
	private static String spaces(int n) {
		return (n <= 0) ? "" : ("  " + spaces(n - 1));
	}
}
