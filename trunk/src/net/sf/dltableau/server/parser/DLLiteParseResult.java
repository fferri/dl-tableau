package net.sf.dltableau.server.parser;

import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.server.parser.ast.AbstractNodeList;

public class DLLiteParseResult {
	protected final AbstractNode formula;
	
	public DLLiteParseResult(AbstractNode formula) {
		this.formula = formula;
	}
	
	public AbstractNode getFormula() {
		return formula;
	}
	
	public static DLLiteParseResult factory(AbstractNode n) {
		if(n instanceof AbstractNodeList) {
			AbstractNodeList l = (AbstractNodeList)n;
			if(l.size() == 1)
				return new DLLiteParseResult(l.get(0));
			else
				return new DLLiteParseResultWithTBOX(l);
		}
		return new DLLiteParseResult(n);
	}
}
