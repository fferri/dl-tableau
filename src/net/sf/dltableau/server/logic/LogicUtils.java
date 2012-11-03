package net.sf.dltableau.server.logic;

import net.sf.dltableau.server.parser.ast.*;

public class LogicUtils {
	public static AbstractNode toNegationNormalForm(AbstractNode n) {
		if(n instanceof Not) {
			Not not = (Not)n;
			AbstractNode x = not.getOp();
			
			if(x instanceof And) {
				And y = (And)x;
				return new Or(toNegationNormalForm(new Not(y.getOp1())), toNegationNormalForm(new Not(y.getOp2())));
			} else if(x instanceof Or) {
				Or y = (Or)x;
				return new And(toNegationNormalForm(new Not(y.getOp1())), toNegationNormalForm(new Not(y.getOp2())));
			} else if(x instanceof ForAll) {
				ForAll y = (ForAll)x;
				return new Exists(y.getRole(), toNegationNormalForm(new Not(y.getExpression())));
			} else if(x instanceof Exists) {
				Exists y = (Exists)x;
				return new ForAll(y.getRole(), toNegationNormalForm(new Not(y.getExpression())));
			} else if(x instanceof Parens) {
				Parens y = (Parens)x;
				return new Parens(toNegationNormalForm(new Not(y.getOp())));
			} else if(x instanceof Atom){
				return n;
			} else if (x instanceof Not) {
				// cancel double negation
				Not not2 = (Not)x;
				return toNegationNormalForm(not2.getOp());
			} else {
				throw new IllegalArgumentException("Cannot handle negation of node of class " + x.getClass());
			}
		} else if(n instanceof And) {
			And m = (And)n;
			return new And(toNegationNormalForm(m.getOp1()), toNegationNormalForm(m.getOp2()));
		} else if(n instanceof Or) {
			Or m = (Or)n;
			return new Or(toNegationNormalForm(m.getOp1()), toNegationNormalForm(m.getOp2()));
		} else if(n instanceof ForAll) {
			ForAll m = (ForAll)n;
			return new ForAll(m.getRole(), toNegationNormalForm(m.getExpression()));
		} else if(n instanceof Exists) {
			Exists m = (Exists)n;
			return new Exists(m.getRole(), toNegationNormalForm(m.getExpression()));
		} else if(n instanceof Parens) {
			Parens m = (Parens)n;
			return new Parens(toNegationNormalForm(m.getOp()));
		} else if(n instanceof Atom) {
			return n;
		} else if(n instanceof AbstractDefinition) {
			return toNegationNormalForm((AbstractDefinition)n);
		} else {
			throw new IllegalArgumentException("Cannot handle node of class " + n.getClass());
		}
	}
	
	public static AbstractDefinition toNegationNormalForm(AbstractDefinition n) {
		if(n instanceof DefinedAs) {
			DefinedAs m = (DefinedAs)n;
			return new DefinedAs(m.getConceptName(), toNegationNormalForm(m.getOp2()));
		} else if(n instanceof SubsumedBy) {
			SubsumedBy m = (SubsumedBy)n;
			return new SubsumedBy(toNegationNormalForm(m.getOp1()), toNegationNormalForm(m.getOp2()));
		} else {
			throw new IllegalArgumentException("Cannot handle node of class " + n.getClass());
		}
	}
	
	public static AbstractNode removeDoubleParentheses(AbstractNode n) {
		if(n instanceof Not) {
			Not not = (Not)n;
			AbstractNode x = not.getOp();
			
			if(x instanceof And) {
				And y = (And)x;
				return new Or(removeDoubleParentheses(new Not(y.getOp1())), removeDoubleParentheses(new Not(y.getOp2())));
			} else if(x instanceof Or) {
				Or y = (Or)x;
				return new And(removeDoubleParentheses(new Not(y.getOp1())), removeDoubleParentheses(new Not(y.getOp2())));
			} else if(x instanceof ForAll) {
				ForAll y = (ForAll)x;
				return new Exists(y.getRole(), removeDoubleParentheses(new Not(y.getExpression())));
			} else if(x instanceof Exists) {
				Exists y = (Exists)x;
				return new ForAll(y.getRole(), removeDoubleParentheses(new Not(y.getExpression())));
			} else if(x instanceof Parens) {
				Parens y = (Parens)x;
				return new Parens(removeDoubleParentheses(new Not(y.getOp())));
			} else if(x instanceof Atom){
				return n;
			} else if (x instanceof Not) {
				// cancel double negation
				Not not2 = (Not)x;
				return removeDoubleParentheses(not2.getOp());
			} else {
				throw new IllegalArgumentException("Cannot handle negation of node of class " + x.getClass());
			}
		} else if(n instanceof And) {
			And m = (And)n;
			return new And(removeDoubleParentheses(m.getOp1()), removeDoubleParentheses(m.getOp2()));
		} else if(n instanceof Or) {
			Or m = (Or)n;
			return new Or(removeDoubleParentheses(m.getOp1()), removeDoubleParentheses(m.getOp2()));
		} else if(n instanceof ForAll) {
			ForAll m = (ForAll)n;
			return new ForAll(m.getRole(), removeDoubleParentheses(m.getExpression()));
		} else if(n instanceof Exists) {
			Exists m = (Exists)n;
			return new Exists(m.getRole(), removeDoubleParentheses(m.getExpression()));
		} else if(n instanceof Parens) {
			Parens m = (Parens)n;
			while(m.getOp() instanceof Parens) m = (Parens)m.getOp();
			return new Parens(removeDoubleParentheses(m.getOp()));
		} else if(n instanceof SubsumedBy) {
			SubsumedBy m = (SubsumedBy)n;
			return new SubsumedBy(removeDoubleParentheses(m.getOp1()), removeDoubleParentheses(m.getOp2()));
		} else if(n instanceof DefinedAs) {
			DefinedAs m = (DefinedAs)n;
			return new DefinedAs(m.getConceptName(), removeDoubleParentheses(m.getOp2()));
		} else if(n instanceof Atom) {
			return n;
		} else {
			throw new IllegalArgumentException("Cannot handle node of class " + n.getClass());
		}	
	}
}
