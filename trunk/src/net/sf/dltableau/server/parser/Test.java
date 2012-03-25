package net.sf.dltableau.server.parser;

import net.sf.dltableau.server.logic.tableau.Transform;
import net.sf.dltableau.server.parser.ast.*;

public class Test {
	public static void main(String[] args) throws Exception {
		String[] tests = {
				"not (a or b or c)",
				"Y or not X",
				"A or B and C",
				"(A or B) and C",
				"forall(X.Y)",
				"forall(X.Y or Z and K)",
				"not a or b",
				"not exists (A . B)",
				"not (a or not (b and c or not (d and not e)))"
		};
		
		for(String test : tests) {
			AbstractNode ast = DLLiteParser.parse(test);
			System.out.println(test);
			//System.out.println(ast.treeString());
			//System.out.println("");
			
			ast = Transform.pushNotInside(ast);
			System.out.println(ast.toString());
			//System.out.println(ast.treeString());
			System.out.println("");
		}
	}
}
