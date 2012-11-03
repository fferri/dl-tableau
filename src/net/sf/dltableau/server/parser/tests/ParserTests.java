package net.sf.dltableau.server.parser.tests;

import net.sf.dltableau.server.logic.LogicUtils;
import net.sf.dltableau.server.logic.render.ASTRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ast.*;

public abstract class ParserTests {
	private static final Atom a = new Atom("a");
	private static final Atom b = new Atom("b");
	private static final Atom c = new Atom("c");
	private static final Atom r = new Atom("r");
	
	private static final Test[] tests = {
		new Test()
			.formula("not exists r.a or b")
			.expectedAST(new Or(new ForAll(r, new Not(a)), b)),
		new Test()
			.formula("a|b|c")
			.expectedAST(new Or(new Or(a, b), c)),
		new Test()
			.formula("!(a|b|c)")
			.expectedAST(new Parens(new And(new And(new Not(a), new Not(b)), new Not(c)))),
		new Test()
			.formula("a|b&c")
			.expectedAST(new Or(a, new And(b, c))),
		new Test()
			.formula("a&b|c")
			.expectedAST(new Or(new And(a, b), c)),
		new Test()
			.formula("(a|b)&c")
			.expectedAST(new And(new Parens(new Or(a, b)), c)),
		new Test()
			.formula("a|!b")
			.expectedAST(new Or(a, new Not(b))),
		new Test()
			.formula("not (a or not (b and c or not (a and not b)))")
			.expectedAST(new Parens(new And(new Not(a), new Parens(new Or(new And(b, c), new Parens(new Or(new Not(a), b))))))),
		new Test()
			.formula("exists r. a subsumed-by b or c")
			.type(Test.Type.DEFINITION)
			.expectedAST(new SubsumedBy(new Exists(r, a), new Or(b, c))),
		new Test()
			.formula("a = b and not exists r . c")
			.type(Test.Type.DEFINITION)
			.expectedAST(new DefinedAs(a, new And(b, new ForAll(r, new Not(c))))),
		new Test()
			.formula("a")
			.type(Test.Type.DEFINITION)
			.expectedAST(new Or(a, new And(b, c)))
			.expectedTestResult(false)
	};
	
	public static void main(String args[]) {
		int pass = 0, fail = 0;
		for(Test test : tests) {
			if(!test.runTest()) {
				System.out.println("FAILED TEST " + test);
				if(!test.verbose)
					test.verbose(true).runTest();
				fail++;
			} else {
				pass++;
			}
		}
		System.out.println("" + pass + " tests passed, " + fail + " tests failed");
	}
}

class Test {
	protected String formula = null;
	public Test formula(String f) {formula = f; return this;}
	
	protected boolean nnf = true;
	public Test nnf(boolean v) {nnf = v; return this;}
	
	protected boolean expectedTestResult = true;
	public Test expectedTestResult(boolean v) {expectedTestResult = v; return this;}
	
	protected AbstractNode expectedAST;
	public Test expectedAST(AbstractNode n) {expectedAST = n; return this;}
	
	public static enum Type {CONCEPT, DEFINITION};
	protected Type type = Type.CONCEPT;
	public Test type(Type t) {type = t; return this;}
	
	protected boolean verbose = false;
	public Test verbose(boolean v) {verbose = v; return this;}
	
	public String toString() {
		return getClass().getSimpleName() + ": " + formula + "";
	}
	
	protected void println(String s) {
		if(verbose) System.out.println(s);
	}
	
	public boolean runTest() {
		try {
			AbstractNode ast =
					type.equals(Type.CONCEPT)
					? DLLiteParser.parseConceptExpression(formula)
					: type.equals(Type.DEFINITION)
					? DLLiteParser.parseDefinition(formula)
					: null;
			
			println(type + ": " + ast.toString());
			
			if(nnf) {
				ast = LogicUtils.toNegationNormalForm(ast);
				println("NNF: " + ast.toString());
			}
			
			println("");
			
			println(ASTRenderer.render(ast, RenderMode.PLAINTEXT));
			
			return ast.equals(expectedAST) == expectedTestResult;
		} catch(Exception e) {
			if(verbose) e.printStackTrace();
			return !expectedTestResult;
		}
	}
}