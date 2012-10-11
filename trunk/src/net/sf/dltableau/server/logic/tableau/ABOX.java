package net.sf.dltableau.server.logic.tableau;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.Atom;
import net.sf.dltableau.server.parser.ast.Parens;

public class ABOX {
	private List<AbstractInstance> aList = new ArrayList<AbstractInstance>();
	
	private List<ABOX> child = new ArrayList<ABOX>(2);
	private ABOX parent = null;
	private int level = 0;
	private int childOrdinal = 0;
	
	private boolean STRIP_PARENS = true;

	public ABOX(ABOX parent) {
		// double linked tree of ABOXes:
		this.parent = parent;
		if(parent != null) parent.child.add(this);
		
		// locate level of this ABOX in the ABOX tree
		ABOX tmp = this;
		while(tmp.parent != null) {
			level++;
			tmp = tmp.parent;
		}
		
		// locate ordinal of this ABOX among the siblings
		if(parent != null)
			childOrdinal = parent.child.indexOf(this);
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
	
	public List<AbstractInstance> instances() {
		return Collections.unmodifiableList(aList);
	}
	
	public List<ABOX> children() {
		return Collections.unmodifiableList(child);
	}
	
	public boolean isLeaf() {
		return child.isEmpty();
	}
	
	public ABOX parent() {
		return parent;
	}
	
	public int level() {
		return level;
	}
	
	public String getName() {
		if(parent != null && parent.child.size() > 1)
			return "A" + level + "." + childOrdinal;
		else
			return "A" + level;
	}

	public void add(AbstractInstance i) {
		if(STRIP_PARENS) i = stripParens(i);
		if(!contains(i, STRIP_PARENS)) {
			aList.add(i);
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
	
	public int getNewIndividual() {
		int max = -1;
		for(int j = 0; j < size(); j++) {
			AbstractInstance i = get(j);
			if(i instanceof ConceptInstance) {
				ConceptInstance ci = (ConceptInstance)i;
				max = Math.max(max, ci.getIndividual());
			} else if(i instanceof RoleInstance) {
				RoleInstance ri = (RoleInstance)i;
				max = Math.max(max, Math.max(ri.getIndividual1(), ri.getIndividual2()));
			}
		}
		return max + 1;
	}
	
	public List<Integer> getMatchingIndividualsByRole(Atom role, int x1) {
		List<Integer> ret = new ArrayList<Integer>();
		for(int j = 0; j < size(); j++) {
			AbstractInstance i = get(j);
			if(i instanceof RoleInstance) {
				RoleInstance ri = (RoleInstance)i;
				if(ri.getIndividual1() == x1 && ri.getRole().equals(role))
					if(!ret.contains(ri.getIndividual2()))
						ret.add(ri.getIndividual2());
			}
		}
		return ret;
	}
	
	public List<Integer> getAllIndividuals() {
		List<Integer> ret = new ArrayList<Integer>();
		for(int j = 0; j < size(); j++) {
			AbstractInstance i = get(j);
			if(i instanceof ConceptInstance) {
				ConceptInstance ci = (ConceptInstance)i;
				if(!ret.contains(ci.getIndividual()))
					ret.add(ci.getIndividual());
			} else if(i instanceof RoleInstance) {
				RoleInstance ri = (RoleInstance)i;
				if(!ret.contains(ri.getIndividual1()))
					ret.add(ri.getIndividual1());
				if(!ret.contains(ri.getIndividual2()))
					ret.add(ri.getIndividual2());
			}
		}
		return ret;
	}
	
	protected static String indent(int level) {
		if(level <= 0) return "";
		else return "  " + indent(--level);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(" = ");
		if(parent != null) sb.append(parent.getName()).append(" U ");
		sb.append(aList.toString());
		if(containsClash()) sb.append(" *");
		return sb.toString();
	}
	
	public String toStringRecursive() {
		StringBuilder sb = new StringBuilder();
		toStringRecursive(sb);
		return toString() + sb.toString();
	}
	
	private String toStringRecursive(StringBuilder sb) {
		for(ABOX abox : child) {
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
		for(AbstractInstance i : aList) sb.append(indent(level) + i + "\n");
		for(ABOX abox : child) abox.treeString(sb);
	}
}
