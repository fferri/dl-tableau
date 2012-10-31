package net.sf.dltableau.server.parser;

import net.sf.dltableau.server.logic.LogicUtils;
import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.ast.*;

public class Test {
	public static void main(String[] args) throws Exception {
		String[] tests = {
				"a subsumed-by b; c === d; not c",
				/*
				"not (a or b or c)",
				"Y or not X",
				"A or B and C",
				"(A or B) and C",
				"forall X . Y",
				"forall X . (Y or Z and K)",
				"not a or b",
				"not (exists A . B)",
				"not (a or not (b and c or not (d and not e)))",
				*/
		};
		
		for(String test : tests) {
			DLLiteParseResult r = DLLiteParser.parse(test);
			AbstractNode ast = r.getFormula();
			TBOX tbox = null;
			if(r instanceof DLLiteParseResultWithTBOX) {
				tbox = ((DLLiteParseResultWithTBOX)r).getTBOX();
				System.out.println("" + tbox);
			}
			
			System.out.println(ast.toString());
			
			ast = LogicUtils.toNegationNormalForm(ast);
			System.out.println(ast.toString());
			System.out.println("");
		}
	}
}
