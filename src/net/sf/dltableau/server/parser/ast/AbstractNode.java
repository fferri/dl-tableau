package net.sf.dltableau.server.parser.ast;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;

public abstract class AbstractNode {
	public abstract boolean isAtomic();
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public String toString() {
		return toString(RenderMode.PLAINTEXT);
	}
	
	public String toString(RenderMode renderMode) {
		return ExpressionRenderer.render(this, renderMode);
	}
}
