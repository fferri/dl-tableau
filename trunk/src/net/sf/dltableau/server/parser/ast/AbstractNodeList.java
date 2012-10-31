package net.sf.dltableau.server.parser.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AbstractNodeList extends AbstractNode implements List<AbstractNode> {
	protected final List<AbstractNode> list; 
	
	public AbstractNodeList() {
		list = new ArrayList<AbstractNode>();
	}

	@Override
	public boolean isAtomic() {
		return false;
	}

	@Override
	public boolean add(AbstractNode n) {
		return list.add(n);
	}

	@Override
	public void add(int pos, AbstractNode n) {
		list.add(pos, n);
	}

	@Override
	public boolean addAll(Collection<? extends AbstractNode> c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int pos, Collection<? extends AbstractNode> c) {
		return list.addAll(pos, c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public AbstractNode get(int pos) {
		return list.get(pos);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<AbstractNode> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<AbstractNode> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<AbstractNode> listIterator(int pos) {
		return list.listIterator(pos);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public AbstractNode remove(int pos) {
		return list.remove(pos);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public AbstractNode set(int pos, AbstractNode n) {
		return list.set(pos, n);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<AbstractNode> subList(int start, int end) {
		return list.subList(start, end);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean equals(Object o) {
		return list.equals(o);
	}
	
	@Override
	public int hashCode() {
		return 17 * list.hashCode() + getClass().hashCode();
	}
}
