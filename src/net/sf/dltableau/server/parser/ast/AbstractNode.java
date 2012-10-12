package net.sf.dltableau.server.parser.ast;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;

public abstract class AbstractNode {
	public abstract boolean isAtomic();
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public String toString() {
		return ExpressionRenderer.render(this, false);
	}
}
