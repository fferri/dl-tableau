package net.sf.dltableau.server.logic.tableau;

import net.sf.dltableau.server.logic.render.RenderMode;

public abstract class AbstractInstance {
	public abstract String toString();
	
	public abstract String toString(RenderMode renderMode);
}
