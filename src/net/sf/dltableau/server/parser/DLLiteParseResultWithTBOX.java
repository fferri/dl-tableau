package net.sf.dltableau.server.parser;

import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.ast.AbstractNodeList;

public class DLLiteParseResultWithTBOX extends DLLiteParseResult {
	protected final TBOX tbox;
	
	public DLLiteParseResultWithTBOX(AbstractNodeList l) {
		super(l.get(l.size() - 1));
		
		tbox = new TBOX(l);
	}
	
	public TBOX getTBOX() {
		return tbox;
	}
}
