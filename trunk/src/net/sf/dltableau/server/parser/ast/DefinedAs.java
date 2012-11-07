package net.sf.dltableau.server.parser.ast;

import net.sf.dltableau.server.logic.LogicUtils;

public class DefinedAs extends AbstractDefinition {
	public DefinedAs(Atom op1, AbstractNode op2) {
		super(op1, op2);
	}
	
	public Atom getConceptName() {
		return (Atom)op1;
	}
	
	@Override
	public Or asNormalForm() {
		return new Or(
				new And(
						LogicUtils.toNegationNormalForm(op1),
						LogicUtils.toNegationNormalForm(op2)
						),
				new And(
						LogicUtils.toNegationNormalForm(new Not(op1)),
						LogicUtils.toNegationNormalForm(new Not(op2))
						)
				);
	}
}
