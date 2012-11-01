package net.sf.dltableau.server.logic.abox;

import java.util.ArrayList;
import java.util.List;

import net.sf.dltableau.server.logic.render.RenderMode;

public abstract class AbstractInstance {
	public abstract String toString();
	
	public abstract String toString(RenderMode renderMode);
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
	
	@SuppressWarnings("unchecked")
	public static <T extends AbstractInstance> List<T> selectByClass(List<AbstractInstance> l, Class<T> c) {
		List<T> r = new ArrayList<T>();
		for(AbstractInstance i : l)
			if(c.isInstance(i))
				r.add((T)i);
		return r;
	}
}
