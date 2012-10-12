package net.sf.dltableau.server.logic.tableau;

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dltableau.server.logic.render.ASTRenderer;
import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.IndividualRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ParseException;
import net.sf.dltableau.server.parser.ast.*;

public class Test {
	private static final boolean PRINT_ABSTRACT_SYNTAX_TREE = true;
	private static final boolean PRINT_NEGATION_NORMAL_FORM = true;
	private static final boolean STEP_BY_STEP_EXPANSION = false;
	private static final boolean PRINT_ALL_MODELS = true;
	
	private static final RenderMode renderMode = RenderMode.UNICODE;
	
	private static final String examples[] = {
		"not(exists X. (C and D and (E or F and G)) and forall X.(not C))",
		"exists R. (forall S. C) and forall R. (exists S. not C)",
		"(exists(S.C) and exists(S.D)) and forall(S.(not C or not D))",
		"exists(R.A) and exists(R.B) and not exists(R.A and B)"
	};
	
	public static void main(String[] args) throws ParseException {
		AbstractNode concept = DLLiteParser.parse(examples[0]);
		out.println("Concept string (parsed): " + ExpressionRenderer.render(concept, renderMode));
		concept = Transform.pushNotInside(concept);
		
		if(PRINT_ABSTRACT_SYNTAX_TREE) {
			out.println("Syntax tree:\n" + ASTRenderer.render(concept, renderMode));
		}
		
		if(PRINT_NEGATION_NORMAL_FORM) {
			out.println("Negation normal form of concept: " + ExpressionRenderer.render(concept, renderMode));
		}
		
		Tableau tableau = new Tableau();
		tableau.init(concept);
		
		if(STEP_BY_STEP_EXPANSION) {
			while(true) {
				out.println(tableau.getABOX().toStringRecursive());
				try {out.println("press a key for next step..."); in.read();} catch (IOException e) {}
				if(!tableau.expandStep()) break;
			}
		} else {
			tableau.expand();
			out.println(tableau.getABOX().toStringRecursive());
		}
		
		if(PRINT_ALL_MODELS) {
			List<ABOX> openBranches = tableau.getOpenBranches();
			if(openBranches.isEmpty())
				out.println("Tableau is closed. No model available.");
			else for(int i = 0; i < openBranches.size(); i++) {
				out.println("-------- Model #" + i + ": ----------------");
				//out.print(openBranches.get(i));
				printModel(openBranches.get(i));
			}
		}
	}
	
	public static void printModel(ABOX abox) {
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
		List<Integer> allI = abox.getAllIndividuals();
		StringBuilder domSB = new StringBuilder();
		for(Integer i : allI)
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
