package net.sf.dltableau.server.logic.tbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.AbstractDefinition;
import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.AbstractNodeList;
import net.sf.dltableau.server.parser.ast.And;
import net.sf.dltableau.server.parser.ast.Atom;
import net.sf.dltableau.server.parser.ast.DefinedAs;
import net.sf.dltableau.server.parser.ast.Exists;
import net.sf.dltableau.server.parser.ast.ForAll;
import net.sf.dltableau.server.parser.ast.Not;
import net.sf.dltableau.server.parser.ast.Or;
import net.sf.dltableau.server.parser.ast.Parens;
import net.sf.dltableau.server.parser.ast.SubsumedBy;

/**
 * Model of a TBOX, containing concept definitions and general axioms.
 * 
 * @author Federico Ferri
 *
 */
public class TBOX implements Iterable<AbstractDefinition> {
	protected final List<AbstractDefinition> tList = new ArrayList<AbstractDefinition>();
	protected final List<DefinedAs> tListDefinitions = new ArrayList<DefinedAs>();
	protected final List<SubsumedBy> tListAxioms = new ArrayList<SubsumedBy>();
	protected final List<Or> tListNormalFormAxioms = new ArrayList<Or>();
	protected final Map<Atom, AbstractNode> tMapDefinitions = new HashMap<Atom, AbstractNode>();
	protected final Map<Atom, AbstractNode> tMapAxioms = new HashMap<Atom, AbstractNode>();

	public TBOX() {}
	
	public TBOX(AbstractNodeList l) {
		for(AbstractNode n : l) {
			if(n instanceof DefinedAs) {
				DefinedAs n1 = (DefinedAs)n;
				tList.add(n1);
				tListDefinitions.add(n1);
				tMapDefinitions.put(n1.getConcept(), n1.getDefinition());
			} else if(n instanceof SubsumedBy) {
				SubsumedBy n1 = (SubsumedBy)n;
				tList.add(n1);
				tListAxioms.add(n1);
				tListNormalFormAxioms.add(n1.asNormalForm());
				tMapAxioms.put(n1.getConcept(), n1.getDefinition());
			}
		}
	}
	
	@Override
	public Iterator<AbstractDefinition> iterator() {
		return tList.iterator();
	}
	
	public AbstractNode getDefinitionBody(Atom conceptName) {
		return tMapDefinitions.get(conceptName);
	}
	
	public AbstractNode getAxiomBody(Atom conceptName) {
		return tMapAxioms.get(conceptName);
	}
	
	public List<Or> getNormalFormAxioms() {
		return Collections.unmodifiableList(tListNormalFormAxioms);
	}
	
	/**
	 * Implements UNFOLDING (macro expansion).
	 * This method, applied on the root concept, is suboptimal.
	 * May yield exponential blow-up.
	 * 
	 * @param n Concept expression
	 * @return Unfolded concept expression
	 */
	public AbstractNode replaceDefinedConcepts(AbstractNode n) {
		if(n instanceof Not) {
			Not m = (Not)n;
			return new Not(replaceDefinedConcepts(m.getOp()));
		} else if(n instanceof And) {
			And m = (And)n;
			return new And(replaceDefinedConcepts(m.getOp1()), replaceDefinedConcepts(m.getOp2()));
		} else if(n instanceof Or) {
			Or m = (Or)n;
			return new Or(replaceDefinedConcepts(m.getOp1()), replaceDefinedConcepts(m.getOp2()));
		} else if(n instanceof ForAll) {
			ForAll m = (ForAll)n;
			return new ForAll(m.getRole(), replaceDefinedConcepts(m.getExpression()));
		} else if(n instanceof Exists) {
			Exists m = (Exists)n;
			return new Exists(m.getRole(), replaceDefinedConcepts(m.getExpression()));
		} else if(n instanceof Parens) {
			Parens m = (Parens)n;
			return new Parens(replaceDefinedConcepts(m.getOp()));
		} else if(n instanceof Atom) {
			Atom m = (Atom)n;
			AbstractNode n1;
			return ((n1 = getDefinitionBody(m)) != null) ? new Parens(n1) : n;
		} else {
			throw new IllegalArgumentException("Cannot handle node of class " + n.getClass());
		}
	}
	
	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(AbstractDefinition d : tList) {
			if(first) first = false;
			else sb.append("; ");
			sb.append(d.toString(renderMode));
		}
		return sb.toString();
	}
}
