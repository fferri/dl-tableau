package net.sf.dltableau.server.logic.tableau;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.dltableau.server.parser.ast.Atom;

public class ABOX {
	private List<AbstractInstance> aList = new ArrayList<AbstractInstance>();
	
	private List<ABOX> child = new ArrayList<ABOX>(2);
	private ABOX parent = null;
	
	public ABOX(ABOX parent) {
		this.parent = parent;
		if(parent != null) parent.child.add(this);
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

	public void add(AbstractInstance i) {
		if(!aList.contains(i)) aList.add(i);
	}
	
	public String toString() {
		return aList.toString();
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
		return aList.contains(i) ||
			(parent != null ? parent.contains(i) : false);
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
	
	public String treeString() {
		StringBuilder sb = new StringBuilder();
		treeString(0, sb);
		return sb.toString();
	}
	
	protected void treeString(int level, StringBuilder sb) {
		for(AbstractInstance i : aList) {
			sb.append(indent(level) + i + "\n");
		}
		for(ABOX abox : child) {
			abox.treeString(level + 1, sb);
		}
	}
}
