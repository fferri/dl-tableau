package net.sf.dltableau.server.parser.ast;

import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;

public abstract class AbstractNode {
	public abstract boolean isAtomic();
	
	public AbstractNode negate() {
		if(this instanceof Not) return ((Not)this).getOp();
		else return new Not(this);
	}
	
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
