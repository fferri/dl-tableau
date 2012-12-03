package net.sf.dltableau.server.logic.tableau.tests;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dltableau.server.logic.LogicUtils;
import net.sf.dltableau.server.logic.abox.ABOX;
import net.sf.dltableau.server.logic.abox.AbstractInstance;
import net.sf.dltableau.server.logic.abox.ConceptInstance;
import net.sf.dltableau.server.logic.abox.Individual;
import net.sf.dltableau.server.logic.abox.RoleInstance;
import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.IndividualRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.logic.tableau.Tableau;
import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ParseException;
import net.sf.dltableau.server.parser.ast.*;

public class TableauTests {
	private static final Test[] test = new Test[]{
		new Test("exists R. D and forall R. (not C)", "D subsumed-by exists R. C", "C = D"),
		new Test("not(exists X. (C and D and (E or F and G)) and forall X.(not C))", "C = D or E", "G subsumed-by D and not E"),
		new Test("exists R. (forall S. C) and forall R. (exists S. (not C))"),
		new Test("(exists S. C and exists S. D) and forall S.(not C or not D))"),
		new Test("exists R. A and exists R. B and not exists R. (A and B)")
	};
	
	public static void main(String[] args) {
		test[0].runTest();
	}
}

class Test {
	private static final RenderMode renderMode = RenderMode.PLAINTEXT;

	public final List<AbstractDefinition> defs = new ArrayList<AbstractDefinition>();
	public final AbstractNode concept;
	
	public Test(String conceptStr, String... defStrs) {
		try {
			concept = DLLiteParser.parseConceptExpression(conceptStr);
		} catch(ParseException e) {
			throw new RuntimeException("Parse exception for concept '" + conceptStr + "': " + e);
		}
		for(String defStr : defStrs) {
			try {
				defs.add(DLLiteParser.parseDefinition(defStr));
			} catch(ParseException e) {
				throw new RuntimeException("Parse exception for definition '" + defStr + "': " + e);
			}
		}
	}
	
	private boolean printModels = false;
	public Test printModels(boolean v) {
		printModels = v;
		return this;
	}
	
	private boolean verbose = false;
	public Test verbose(boolean v) {
		verbose = v;
		return this;
	}
	
	public boolean runTest() {
		TBOX tbox = new TBOX(defs);
		if(verbose) {
			out.println("TBOX: " + tbox);		
			out.println("Concept string (parsed): " + ExpressionRenderer.render(concept, renderMode));
		}
		AbstractNode conceptNNF = LogicUtils.toNegationNormalForm(concept);
		
		Tableau tableau = new Tableau();
		tableau.init(tbox, conceptNNF);
		
		tableau.expand();
		if(verbose) out.println(tableau.getABOX().toStringRecursive());
		
		if(printModels) {
			List<ABOX> openBranches = tableau.getOpenBranches();
			if(openBranches.isEmpty())
				out.println("Tableau is closed. No model exists.");
			else for(int i = 0; i < openBranches.size(); i++) {
				out.println("-------- Model #" + i + ": ----------------");
				printModel(openBranches.get(i));
			}
		}
		
		// TODO: compare with expected result
		return true;
	}
	
	private static void printModel(ABOX abox) {
		Map<Atom, List<AbstractInstance>> modelMap = new HashMap<Atom, List<AbstractInstance>>();
		for(int j = 0; j < abox.size(); j++) {
			AbstractInstance i = abox.get(j);
			if(i instanceof ConceptInstance) {
				ConceptInstance ci = (ConceptInstance)i;
				if(!ci.isAtomic()) continue;
				AbstractNode n = ci.getConcept();
				if(n instanceof Atom) {
					Atom a = (Atom)n;
					if(modelMap.get(a) == null) modelMap.put(a, new ArrayList<AbstractInstance>());
					modelMap.get(a).add(ci);
				}
			} else if(i instanceof RoleInstance) {
				RoleInstance ri = (RoleInstance)i;
				Atom a = ri.getRole();
				if(modelMap.get(a) == null) modelMap.put(a, new ArrayList<AbstractInstance>());
				modelMap.get(a).add(ri);
			}
		}
		Set<Individual> allI = abox.getIndividuals(true);
		StringBuilder domSB = new StringBuilder();
		for(Individual i : allI)
			domSB.append(domSB.length() == 0 ? "" : ", ").append(IndividualRenderer.render(i, renderMode));
		System.out.println("DOMAIN = {" + domSB + "}");
		for(Map.Entry<Atom, List<AbstractInstance>> e : modelMap.entrySet()) {
			StringBuilder sb = new StringBuilder();
			for(AbstractInstance ai : e.getValue()) {
				sb.append(sb.length() == 0 ? "" : ", ");
				if(ai instanceof ConceptInstance)
					sb.append(IndividualRenderer.render(((ConceptInstance)ai).getIndividual(), renderMode));
				else if(ai instanceof RoleInstance)
					sb.append(ExpressionRenderer.renderTuple(((RoleInstance)ai), renderMode));
			}
			System.out.println(e.getKey() + " = {" + sb + "}");
		}
	}
}