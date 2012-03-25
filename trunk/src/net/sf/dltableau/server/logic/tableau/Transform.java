package net.sf.dltableau.server.logic.tableau;

import net.sf.dltableau.server.parser.ast.*;

public class Transform {
	public static AbstractNode pushNotInside(AbstractNode n) {
		if(n instanceof Not) {
			Not not = (Not)n;
			AbstractNode x = not.getOp();
			
			if(x instanceof And) {
				And y = (And)x;
				return new Or(pushNotInside(new Not(y.getOp1())), pushNotInside(new Not(y.getOp2())));
			} else if(x instanceof Or) {
				Or y = (Or)x;
				return new And(pushNotInside(new Not(y.getOp1())), pushNotInside(new Not(y.getOp2())));
			} else if(x instanceof ForAll) {
				ForAll y = (ForAll)x;
				return new Exists(y.getRole(), pushNotInside(new Not(y.getExpression())));
			} else if(x instanceof Exists) {
				Exists y = (Exists)x;
				return new ForAll(y.getRole(), pushNotInside(new Not(y.getExpression())));
			} else if(x instanceof SubsumedBy) {
				SubsumedBy y = (SubsumedBy)x;
				return new SubsumedBy(y.getOp1(), pushNotInside(new Not(y.getOp2())));
			} else if(x instanceof Parens) {
				Parens y = (Parens)x;
				return new Parens(pushNotInside(new Not(y.getOp())));
			} else if(x instanceof Atom){
				return n;
			} else if (x instanceof Not) {
				// cancel double negation
				Not not2 = (Not)x;
				return pushNotInside(not2.getOp());
			} else {
				throw new RuntimeException("Cannot handle negation of node of class " + x.getClass());
			}
		} else if(n instanceof And) {
			And m = (And)n;
			return new And(pushNotInside(m.getOp1()), pushNotInside(m.getOp2()));
		} else if(n instanceof Or) {
			Or m = (Or)n;
			return new Or(pushNotInside(m.getOp1()), pushNotInside(m.getOp2()));
		} else if(n instanceof ForAll) {
			ForAll m = (ForAll)n;
			return new ForAll(m.getRole(), pushNotInside(m.getExpression()));
		} else if(n instanceof Exists) {
			Exists m = (Exists)n;
			return new Exists(m.getRole(), pushNotInside(m.getExpression()));
		} else if(n instanceof SubsumedBy) {
			SubsumedBy m = (SubsumedBy)n;
			return new SubsumedBy(pushNotInside(m.getOp1()), pushNotInside(m.getOp2()));
		} else if(n instanceof Parens) {
			Parens m = (Parens)n;
			return new Parens(pushNotInside(m.getOp()));
		} else if(n instanceof Atom) {
			return n;
		} else {
			throw new RuntimeException("Cannot handle node of class " + n.getClass());
		}
	}
}
