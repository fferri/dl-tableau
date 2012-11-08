package net.sf.dltableau.server.logic.abox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.ast.Atom;

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
	protected final List<ConceptInstance> aListConcepts = new ArrayList<ConceptInstance>();
	protected final List<RoleInstance> aListRoles = new ArrayList<RoleInstance>();
    protected final Set<Individual> aSetIndividuals = new HashSet<Individual>();
	
	protected final List<ABOX> children = new ArrayList<ABOX>(2);
	protected final ABOX parent;
	
	protected long nextId = 1; // used only in toplevel ABOX, via nextId()
	protected final long id;
	
	protected long nextInstanceId = 1;

	public ABOX(ABOX parent) {
		this(parent, parent != null ? parent.getNextId() : 1);
		nextId = 2;
	}
	
	protected ABOX(ABOX parent, long id) {
		// double linked tree of ABOXes:
		this.parent = parent;
		if(parent != null) parent.children.add(this);
		
		this.id = id;
	}
	
	protected long getNextId() {
		if(parent != null) return parent.getNextId();
		else return nextId++;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public Iterator<AbstractInstance> iterator() {
		return getInstances(true).iterator();
	}
	
	public List<AbstractInstance> getInstances(boolean recursively) {
		List<AbstractInstance> r = new ArrayList<AbstractInstance>();
		r.addAll(aList);
		if(recursively && parent != null) r.addAll(parent.getInstances(true));
		return r;
	}
	
	public List<ConceptInstance> getConceptInstances(boolean recursively) {
		List<ConceptInstance> r = new ArrayList<ConceptInstance>();
		r.addAll(aListConcepts);
		if(recursively && parent != null) r.addAll(parent.getConceptInstances(true));
		return r;
	}
	
	public List<ConceptInstance> getConceptInstancesByIndividual(Individual i, boolean recursively) {
		List<ConceptInstance> r = new ArrayList<ConceptInstance>();
		for(ConceptInstance inst : aListConcepts)
			if(inst.getIndividual().equals(i))
				r.add(inst);
		if(recursively && parent != null) r.addAll(parent.getConceptInstancesByIndividual(i, true));
		return r;
	}
	
	public List<RoleInstance> getRoleInstances(boolean recursively) {
		List<RoleInstance> r = new ArrayList<RoleInstance>();
		r.addAll(aListRoles);
		if(recursively && parent != null) r.addAll(parent.getRoleInstances(true));
		return r;
	}
	
	public Set<Individual> getIndividuals(boolean recursively) {
		Set<Individual> r = new HashSet<Individual>();
		r.addAll(aSetIndividuals);
		if(recursively && parent != null) r.addAll(parent.getIndividuals(true));
		return r;
	}
	
	public List<ABOX> getChildren() {
		return children;
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}
	
	public ABOX getParent() {
		return parent;
	}
	
	public int getLevel() {
		if(parent == null) return 0;
		else return 1 + parent.getLevel();
	}
	
	public String getName() {
		return "A" + id;
	}

	public void add(AbstractInstance i) {
		if(i instanceof ConceptInstance) {
			ConceptInstance ci = (ConceptInstance)i;
			addConceptInstance(ci);
		} else if(i instanceof RoleInstance) {
			RoleInstance ri = (RoleInstance)i;
			addRoleInstance(ri);
		}
	}
	
	private void addConceptInstance(ConceptInstance ci) {
		ci = ci.removeParentheses();
		if(contains(ci)) return;
		ci.setId(nextInstanceId++);
		aList.add(ci);
		aListConcepts.add(ci);
		aSetIndividuals.add(ci.getIndividual());
	}
	
	private void addRoleInstance(RoleInstance ri) {
		if(contains(ri)) return;
		ri.setId(nextInstanceId++);
		aList.add(ri);
		aListRoles.add(ri);
		aSetIndividuals.add(ri.getIndividual1());
		aSetIndividuals.add(ri.getIndividual2());
	}
	
	public int size() {
		return aList.size() +
			(parent != null ? parent.size() : 0);
	}
	
	public int getNumConceptInstances() {
		return aListConcepts.size() +
			(parent != null ? parent.getNumConceptInstances() : 0);
	}
	
	public int getNumRoleInstances() {
		return aListRoles.size() +
			(parent != null ? parent.getNumRoleInstances() : 0);
	}
	
	public AbstractInstance get(int i) {
		if(i < 0) throw new ArrayIndexOutOfBoundsException();
		if(i < aList.size()) return aList.get(i);
		if(parent == null) throw new ArrayIndexOutOfBoundsException();
		return parent.get(i - aList.size());
	}
	
	public AbstractInstance getInstanceById(long id) {
		for(AbstractInstance i : aList)
			if(i.getId() == id)
				return i;
		if(parent != null)
			return parent.getInstanceById(id);
		return null;
	}
	
	public ConceptInstance getConceptInstance(int i) {
		if(i < 0) throw new ArrayIndexOutOfBoundsException();
		if(i < aListConcepts.size()) return aListConcepts.get(i);
		if(parent == null) throw new ArrayIndexOutOfBoundsException();
		return parent.getConceptInstance(i - aListConcepts.size());
	}
	
	public RoleInstance getRoleInstance(int i) {
		if(i < 0) throw new ArrayIndexOutOfBoundsException();
		if(i < aListRoles.size()) return aListRoles.get(i);
		if(parent == null) throw new ArrayIndexOutOfBoundsException();
		return parent.getRoleInstance(i - aListRoles.size());
	}
	
	public boolean contains(AbstractInstance i) {
		if(i instanceof ConceptInstance) {
			ConceptInstance ci = (ConceptInstance)i;
			return containsConceptInstance(ci);
		} else if(i instanceof RoleInstance) {
			RoleInstance ri = (RoleInstance)i;
			return containsRoleInstance(ri);
		} else {
			return false;
		}
	}
	
	private boolean containsConceptInstance(ConceptInstance ci) {
		ci = ci.removeParentheses();
		return aListConcepts.contains(ci) ||
				(parent != null ? parent.containsConceptInstance(ci) : false);
	}
	
	private boolean containsRoleInstance(RoleInstance ri) {
		return aListRoles.contains(ri) ||
				(parent != null ? parent.containsRoleInstance(ri) : false);
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
		List<RoleInstance> r = RoleInstance.selectRoleInstances(getRoleInstances(true), i, null);
		return RoleInstance.projectIndividuals(r, RoleInstance.Side.S2);
	}
	
	public Individual getNewIndividual() {
		return Individual.newIndividual(this);
	}
	
	public List<Individual> getAncestors(Individual i) {
		Set<Individual> s = new HashSet<Individual>();
		getAncestors(i, getRoleInstances(true), s);
		return new ArrayList<Individual>(s);
	}
	
	private void getAncestors(Individual i, List<RoleInstance> allRoleInstances, Set<Individual> result) {
		List<RoleInstance> l = RoleInstance.selectRoleInstances(allRoleInstances, null, i);
		List<Individual> a1 = RoleInstance.projectIndividuals(l, RoleInstance.Side.S1);
		result.addAll(a1);
		for(Individual i1 : a1)
			getAncestors(i1, allRoleInstances, result);
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
			sb.append("\n");
			for(int i = 0; i < abox.getLevel(); i++) sb.append("  ");
			sb.append(abox.toString());
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
		String indent = ""; for(int i = 0; i < getLevel(); i++) indent += "  ";
		for(AbstractInstance i : aList) sb.append(indent + i + "\n");
		for(ABOX abox : children) abox.treeString(sb);
	}
}
