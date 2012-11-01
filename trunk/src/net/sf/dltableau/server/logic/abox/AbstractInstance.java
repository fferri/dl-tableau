package net.sf.dltableau.server.logic.abox;

import net.sf.dltableau.server.logic.render.RenderMode;

public abstract class AbstractInstance {
	public abstract String toString();
	
	public abstract String toString(RenderMode renderMode);
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
}
