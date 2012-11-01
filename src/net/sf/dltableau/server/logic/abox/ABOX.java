package net.sf.dltableau.server.logic.abox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.Atom;
import net.sf.dltableau.server.parser.ast.Parens;

/**
 * Model of an ABOX, containing instances (of concepts and of roles).
 * Used by Tableau to incrementally build a model.
 * 
 * ABOX is hierarchical: it implicitly contains its ancestor's contents.
 * 
 * @author Federico Ferri
 *
 */
public class ABOX implements Iterable<AbstractInstance> {
	protected final List<AbstractInstance> aList = new ArrayList<AbstractInstance>();
	protected final List<RoleInstance> aListRoles = new ArrayList<RoleInstance>();
	protected final List<ConceptInstance> aListConcepts = new ArrayList<ConceptInstance>();
	protected final Map<Individual, AbstractInstance> aMapInstances = new HashMap<Individual, AbstractInstance>();
	protected final Set<Individual> aSetIndividuals = new HashSet<Individual>();
	
	protected final List<ABOX> children = new ArrayList<ABOX>(2);
	protected final ABOX parent;
	protected final int level;
	protected final int childOrdinal;
	
	protected static final boolean STRIP_PARENS = true;

	public ABOX(ABOX parent) {
		// double linked tree of ABOXes:
		this.parent = parent;
		if(parent != null) parent.children.add(this);
		
		// locate level of this ABOX in the ABOX tree
		ABOX tmp = this;
		int level_ = 0;
		while(tmp.parent != null) {
			level_++;
			tmp = tmp.parent;
		}
		level = level_;
		
		// locate ordinal of this ABOX among the siblings
		childOrdinal = (parent != null) ? parent.children.indexOf(this) : 0;
	}
	
	private AbstractNode stripParens(AbstractNode n) {
		while(n instanceof Parens) n = ((Parens)n).getOp();
		return n;
	}
	
	private AbstractInstance stripParens(AbstractInstance i) {
		if(i instanceof ConceptInstance) {
			ConceptInstance ci = (ConceptInstance)i;
			return new ConceptInstance(stripParens(ci.getConcept()), ci.getIndividual());
		} else {
			return i;
		}
	}
	
	@Override
	public Iterator<AbstractInstance> iterator() {
		return getInstances().iterator();
	}
	
	public List<AbstractInstance> getInstances() {
		return Collections.unmodifiableList(aList);
	}
	
	public List<RoleInstance> getRoleInstances() {
		return Collections.unmodifiableList(aListRoles);
	}
	
	public List<ConceptInstance> getConceptInstances() {
		return Collections.unmodifiableList(aListConcepts);
	}
	
	public List<ABOX> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}
	
	public ABOX getParent() {
		return parent;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getName() {
		if(parent != null && parent.children.size() > 1)
			return "A" + level + "." + childOrdinal;
		else
			return "A" + level;
	}

	public void add(AbstractInstance i) {
		if(STRIP_PARENS) i = stripParens(i);
		if(!contains(i, STRIP_PARENS)) {
			aList.add(i);
			if(i instanceof ConceptInstance) {
				ConceptInstance ci = (ConceptInstance)i;
				aListConcepts.add(ci);
				aMapInstances.put(ci.getIndividual(), ci);
				aSetIndividuals.add(ci.getIndividual());
			} else if(i instanceof RoleInstance) {
				RoleInstance ri = (RoleInstance)i;
				aListRoles.add(ri);
				aMapInstances.put(ri.getIndividual1(), ri);
				aSetIndividuals.add(ri.getIndividual1());
				aSetIndividuals.add(ri.getIndividual2());
			}
		}
	}
	
	public int size() {
		return aList.size() +
			(parent != null ? parent.size() : 0);
	}
	
	public AbstractInstance get(int i) {
		if(i < 0) throw new ArrayIndexOutOfBoundsException();
		if(i < aList.size()) return aList.get(i);
		if(parent == null) throw new ArrayIndexOutOfBoundsException();
		return parent.get(i - aList.size());
	}
	
	public boolean contains(AbstractInstance i) {
		if(STRIP_PARENS) i = stripParens(i);
		return contains(i, STRIP_PARENS);
	}
	
	private boolean contains(AbstractInstance i, boolean stripParens) {
		return aList.contains(i) ||
				(parent != null ? parent.contains(i, stripParens) : false);
	}
	
	public boolean containsClash() {
		for(int j = 0; j < size(); j++) {
			AbstractInstance i = get(j);
			if(i instanceof ConceptInstance) {
				ConceptInstance ci = (ConceptInstance)i;
				if(!ci.isAtomic()) continue;
				if(contains(ci.negatedInstance())) return true;
			}
		}
		return false;
	}
	
	public List<Individual> getMatchingIndividualsByRole(Atom role, Individual i) {
		List<RoleInstance> r = RoleInstance.selectRoleInstances(aListRoles, i, null);
		return RoleInstance.projectIndividuals(r, RoleInstance.Side.S2);
	}
	
	public List<Individual> getAllIndividuals() {
		return new ArrayList<Individual>(aSetIndividuals);
	}
	
	public Individual getNewIndividual() {
		return Individual.newIndividual(this);
	}
	
	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(" = ");
		if(parent != null) sb.append(parent.getName()).append(" U ");
		sb.append("{");
		boolean first = true;
		for(AbstractInstance n : aList) {
			if(first) first = false;
			else sb.append(", ");
			sb.append(n.toString(renderMode));
		}
		sb.append("}");
		if(containsClash()) sb.append(" *");
		return sb.toString();
	}

	public String toStringRecursive() {
		return toStringRecursive(RenderMode.PLAINTEXT);
	}
	
	public String toStringRecursive(RenderMode renderMode) {
		StringBuilder sb = new StringBuilder();
		toStringRecursive(sb);
		return toString() + sb.toString();
	}
	
	private String toStringRecursive(StringBuilder sb) {
		for(ABOX abox : children) {
			sb.append("\n").append(abox.toString());
			abox.toStringRecursive(sb);
		}
		return sb.toString();
	}
	
	public String treeString() {
		StringBuilder sb = new StringBuilder();
		treeString(sb);
		return sb.toString();
	}
	
	protected void treeString(StringBuilder sb) {
		String indent = ""; for(int i = 0; i < level; i++) indent += "  ";
		for(AbstractInstance i : aList) sb.append(indent + i + "\n");
		for(ABOX abox : children) abox.treeString(sb);
	}
}
