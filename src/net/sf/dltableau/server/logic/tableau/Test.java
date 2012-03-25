package net.sf.dltableau.server.logic.tableau;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ParseException;
import net.sf.dltableau.server.parser.ast.*;

public class Test {
	public static void main(String[] args) throws ParseException {
		//String conceptStr = "exists(R.A) and exists(R.B) and not exists(R.A and B)";
		//String conceptStr = "(exists(S.C) and exists(S.D)) and forall(S.(not C or not D))";
		String conceptStr = "exists R. (forall S. C) and forall R. (exists S. not C)";
		System.out.println("Concept string: " + conceptStr);
		AbstractNode concept = DLLiteParser.parse(conceptStr);
		System.out.println("Parsed concept string: " + concept);
		System.out.println("Syntax tree:\n" + concept.treeString());
		concept = Transform.pushNotInside(concept);
		System.out.println("Negation normal form of concept: " + concept);
		
		Tableau tableau = new Tableau();
		tableau.init(concept);
		/*
		tableau.expand();

		System.out.println("Tableau:");
		System.out.println(tableau.abox().treeString());
		*/
		
		while(true) {
			System.out.println(tableau.abox().treeString());
			try {System.in.read();} catch (IOException e) {}
			if(!tableau.expandStep()) break;
		}
		
		/*
			System.out.println("ABOX:");
			ABOX abox = aboxes.get(0);
			for(int j = 0; j < abox.size(); j++) {
				System.out.println("  " + j + ": " + abox.get(j));
			}
			System.out.println("Model:");
			printModel(abox);
		*/
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
			domSB.append(domSB.length() == 0 ? "" : ", ").append(AbstractInstance.getIndividualString(i));
		System.out.println("DOMAIN = {" + domSB + "}");
		for(Map.Entry<Atom, List<AbstractInstance>> e : modelMap.entrySet()) {
			StringBuilder sb = new StringBuilder();
			for(AbstractInstance ai : e.getValue()) {
				sb.append(sb.length() == 0 ? "" : ", ");
				if(ai instanceof ConceptInstance)
					sb.append(AbstractInstance.getIndividualString(((ConceptInstance)ai).getIndividual()));
				else if(ai instanceof RoleInstance)
					sb.append(((RoleInstance)ai).toString2());
			}
			System.out.println(e.getKey() + " = {" + sb + "}");
		}
	}
}
