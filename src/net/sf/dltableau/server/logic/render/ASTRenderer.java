package net.sf.dltableau.server.logic.render;

import net.sf.dltableau.server.parser.ast.AbstractBinOp;
import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.AbstractQuantifier;
import net.sf.dltableau.server.parser.ast.AbstractUnOp;
import net.sf.dltableau.server.parser.ast.Atom;

public class ASTRenderer {
	public static String render(AbstractNode ast, RenderMode renderMode) {
		StringBuilder sb = new StringBuilder();
		render(ast, renderMode, sb, 0);
		return sb.toString();
	}
	
	private static void render(AbstractNode n, RenderMode renderMode, StringBuilder sb, int level) {
		if(n instanceof AbstractBinOp) render((AbstractBinOp)n, renderMode, sb, level);
		else if(n instanceof AbstractUnOp) render((AbstractUnOp)n, renderMode, sb, level);
		else if(n instanceof AbstractQuantifier) render((AbstractQuantifier)n, renderMode, sb, level);
		else if(n instanceof Atom) render((Atom)n, renderMode, sb, level);
		else throw new IllegalArgumentException();
	}
	
	private static void render(AbstractBinOp n, RenderMode renderMode, StringBuilder sb, int level) {
		if(renderMode.isHTML()) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(OperatorRenderer.render(n, renderMode));
		
		if(renderMode.isHTML()) sb.append("</td><td></td></tr><tr><td></td><td style=\"border-left: 1px solid black;\">");
		else sb.append("\n");
		
		render(n.getOp1(), renderMode, sb, level + 1);
		render(n.getOp2(), renderMode, sb, level + 1);
		
		if(renderMode.isHTML()) sb.append("</td></tr></table>");
	}

	private static void render(AbstractUnOp n, RenderMode renderMode, StringBuilder sb, int level) {
		if(renderMode.isHTML()) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(OperatorRenderer.render(n, renderMode));
		
		if(renderMode.isHTML()) sb.append("</td><td></td></tr><tr><td></td><td style=\"border-left: 1px solid black;\">");
		else sb.append("\n");
		
		render(n.getOp(), renderMode, sb, level + 1);
		
		if(renderMode.isHTML()) sb.append("</td></tr></table>");
	}

	private static void render(AbstractQuantifier n, RenderMode renderMode, StringBuilder sb, int level) {
		if(renderMode.isHTML()) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(OperatorRenderer.render(n, renderMode));
		
		if(renderMode.isHTML()) sb.append("</td><td></td></tr><tr><td></td><td style=\"border-left: 1px solid black;\">");
		else sb.append("\n");
		
		render(n.getRole(), renderMode, sb, level + 1);
		render(n.getExpression(), renderMode, sb, level + 1);
		
		if(renderMode.isHTML()) sb.append("</td></tr></table>");
	}

	private static void render(Atom n, RenderMode renderMode, StringBuilder sb, int level) {
		if(renderMode.isHTML()) sb.append("<table><tr><td>");
		else sb.append(spaces(level));
		
		sb.append(n.getName());
		
		if(renderMode.isHTML()) sb.append("</td></tr></table>");
		else sb.append("\n");
	}
	
	private static String spaces(int n) {
		return (n <= 0) ? "" : ("  " + spaces(n - 1));
	}
}
