package net.sf.dltableau.server.parser.tests;

import net.sf.dltableau.server.logic.LogicUtils;
import net.sf.dltableau.server.logic.render.ASTRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.DLLiteParseResult;
import net.sf.dltableau.server.parser.DLLiteParseResultWithTBOX;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ast.*;

public class ParserTest {
	private static final Atom a = new Atom("a");
	private static final Atom b = new Atom("b");
	private static final Atom c = new Atom("c");
	private static final Atom r = new Atom("r");
	
	private static final ParserTest[] tests = {
		new ParserTest("not exists r.a or b",
			new Or(new ForAll(r, new Not(a)), b)),
		new ParserTest("a|b|c",
			new Or(new Or(a, b), c)),
		new ParserTest("!(a|b|c)",
			new Parens(new And(new And(new Not(a), new Not(b)), new Not(c)))),
		new ParserTest("a|b&c",
			new Or(a, new And(b, c))),
		new ParserTest("a&b|c",
			new Or(new And(a, b), c)),
		new ParserTest("(a|b)&c",
			new And(new Parens(new Or(a, b)), c)),
		new ParserTest("a|!b",
			new Or(a, new Not(b))),
		new ParserTest("not (a or not (b and c or not (a and not b)))",
			new Parens(new And(new Not(a), new Parens(new Or(new And(b, c), new Parens(new Or(new Not(a), b))))))),
		//new ParserTest("a subsumed-by b; c = d; not exists r . c"),
	};
	
	private static final boolean VERBOSE = false;
	
	public static void main(String args[]) {
		int pass = 0, fail = 0;
		for(ParserTest test : tests) {
			if(!test.runTest()) {
				System.out.println("FAILED TEST " + test);
				fail++;
			} else {
				pass++;
			}
		}
		System.out.println("" + pass + " tests passed, " + fail + " tests failed");
	}
	
	private static void println(String s) {
		if(VERBOSE) System.out.println(s);
	}
	
	private final String formula;
	private final AbstractNode expectedNNF;
	
	private ParserTest(String formula, AbstractNode expectedNNF) {
		this.formula = formula;
		this.expectedNNF = expectedNNF;
	}
	
	private boolean runTest() {
		try {
			DLLiteParseResult r = DLLiteParser.parse(formula);
			AbstractNode ast = r.getFormula();
			TBOX tbox = null;
			if(r instanceof DLLiteParseResultWithTBOX) {
				tbox = ((DLLiteParseResultWithTBOX)r).getTBOX();
				println("TBOX: " + tbox);
			}
			
			println("CONCEPT: " + ast.toString());
			
			ast = LogicUtils.toNegationNormalForm(ast);
			println("NNF: " + ast.toString());
			println("");
			
			println(ASTRenderer.render(ast, RenderMode.PLAINTEXT));
			
			return ast.equals(expectedNNF);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + formula + "";
	}
}
