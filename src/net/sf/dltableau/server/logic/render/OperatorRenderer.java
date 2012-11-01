package net.sf.dltableau.server.logic.render;

import java.util.HashMap;
import java.util.Map;

import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.And;
import net.sf.dltableau.server.parser.ast.DefinedAs;
import net.sf.dltableau.server.parser.ast.Exists;
import net.sf.dltableau.server.parser.ast.ForAll;
import net.sf.dltableau.server.parser.ast.Not;
import net.sf.dltableau.server.parser.ast.Or;
import net.sf.dltableau.server.parser.ast.Parens;
import net.sf.dltableau.server.parser.ast.SubsumedBy;

public class OperatorRenderer {
	public static String render(AbstractNode n, RenderMode renderMode) {
		return map.get(renderMode).get(n.getClass());
	}
	
	private static final Map<RenderMode, Map<Class<? extends AbstractNode>, String>> map;
	
	static {
		map = new HashMap<RenderMode, Map<Class<? extends AbstractNode>, String>>();
		
		Map<Class<? extends AbstractNode>, String> plaintext = new HashMap<Class<? extends AbstractNode>, String>();
		plaintext.put(And.class, " and ");
		plaintext.put(Or.class, " or ");
		plaintext.put(Not.class, "not ");
		plaintext.put(ForAll.class, "forall ");
		plaintext.put(Exists.class, "exists ");
		plaintext.put(SubsumedBy.class, " subsumed-by ");
		plaintext.put(DefinedAs.class, " = ");
		plaintext.put(Parens.class, "(...)");
		map.put(RenderMode.PLAINTEXT, plaintext);
		
		Map<Class<? extends AbstractNode>, String> unicode = new HashMap<Class<? extends AbstractNode>, String>();
		unicode.put(And.class, " \u2293 ");
		unicode.put(Or.class, " \u2294 ");
		unicode.put(Not.class, "\u00AC");
		unicode.put(ForAll.class, "\u2200");
		unicode.put(Exists.class, "\u2203");
		unicode.put(SubsumedBy.class, " \u2291 ");
		unicode.put(DefinedAs.class, " \u2261 ");
		unicode.put(Parens.class, "(...)");
		map.put(RenderMode.UNICODE, unicode);
		
		Map<Class<? extends AbstractNode>, String> html = new HashMap<Class<? extends AbstractNode>, String>();
		html.put(And.class, " &#x2293; ");
		html.put(Or.class, " &#x2294; ");
		html.put(Not.class, "&not;");
		html.put(ForAll.class, "&forall;");
		html.put(Exists.class, "&exist;");
		html.put(SubsumedBy.class, " &#x2291; ");
		html.put(DefinedAs.class, " &#x2261; ");
		html.put(Parens.class, "(...)");
		map.put(RenderMode.HTML, html);
	}
}
