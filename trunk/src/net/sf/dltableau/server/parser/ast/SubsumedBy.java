package net.sf.dltableau.server.parser.ast;

import net.sf.dltableau.server.logic.LogicUtils;

public class SubsumedBy extends AbstractDefinition {
	public SubsumedBy(AbstractNode op1, AbstractNode op2) {
		super(op1, op2);
	}
	
	@Override
	public Or asNormalForm() {
		return new Or(
				LogicUtils.toNegationNormalForm(new Not(op1)),
				LogicUtils.toNegationNormalForm(op2)
				);
	}
}
