package net.sf.dltableau.server.parser.ast;

public class Atom extends AbstractNode {
	protected String name;
	
	public Atom(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isAtomic() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Atom) {
			Atom a = (Atom)obj;
			return a.name.equals(name);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return 17 * name.hashCode() + getClass().hashCode();
	}
}
