package net.sf.dltableau.server.logic.tableau;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.dltableau.server.logic.abox.ABOX;
import net.sf.dltableau.server.logic.abox.ConceptInstance;
import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ParseException;
import net.sf.dltableau.server.parser.ast.AbstractDefinition;
import net.sf.dltableau.server.parser.ast.AbstractNode;

public final class InteractiveCLI {
	public static void main(String[] args) {
		new InteractiveCLI().mainMenu();
	}
	
	private InteractiveCLI() {}
	
	List<AbstractDefinition> defs = new ArrayList<AbstractDefinition>();
	AbstractNode concept = null;
	
	{try {
		//defs.add(DLLiteParser.parseDefinition("human subsumed-by exists hasFather.human"));
		//concept = DLLiteParser.parseConceptExpression("exists human.(not exists hasFather.human)");
		
		//concept = DLLiteParser.parseConceptExpression("exists R. C and exists R. D and forall R. (not C and not D)");
		
		defs.add(DLLiteParser.parseDefinition("D subsumed-by  exists R . C"));
		defs.add(DLLiteParser.parseDefinition("C = D"));
		concept = DLLiteParser.parseConceptExpression("exists R. D and forall R. (not C)");
	} catch (ParseException e) {e.printStackTrace();System.exit(1);}}
	
	protected static void println(String message) {
		System.out.println(message);
	}
	
	protected static void print(String message) {
		System.out.print(message);
	}
	
	void printTBOX() {
		println("TBOX:");
		for(AbstractDefinition def : defs) println("   " + def);
		if(defs.isEmpty()) println("   <empty>");
	}
	
	void printConcept() {
		println("Concept:");
		if(concept != null) println("   " + concept);
		else println("   <not set>");
	}
	
	void mainMenu() {
		while(true) {
			printTBOX();
			printConcept();
			println("");
			println("1) Edit the TBOX");
			println("2) Set the concept expression");
			println("3) Start interactive expansion");
			println("4) Quit");
			int choice = new ConstrainedIntegerReader(1, 4).askForInteger("Enter your choice: ");
			switch(choice) {
			case 1: tboxEditMenu(); break;
			case 2: setConcept(); break;
			case 3: interactiveExpansion(); break;
			case 4: return;
			}
		}
	}
	
	void tboxEditMenu() {
		while(true) {
			printTBOX();
			println("");
			println("1) Add a definition");
			println("2) Remove a definition");
			println("3) Back to main menu");
			int choice = new ConstrainedIntegerReader(1, 3).askForInteger("Enter your choice: ");
			switch(choice) {
			case 1: addTBOXDef(); break;
			case 2: removeTBOXDef(); break;
			case 3: return;
			}
		}
	}
	
	void addTBOXDef() {
		defs.add(new DefinitionReader().askForDefinition("Enter a DL definition: "));
	}
	
	void removeTBOXDef() {
		for(int i = 0; i < defs.size(); )
			println((i + 1) + ") " + defs.get(i));
		int choice = new ConstrainedIntegerReader(1, defs.size()).askForInteger("Which definition do you want to remove? ");
		defs.remove(choice - 1);
	}
	
	void setConcept() {
		printConcept();
		println("");
		concept = new ConceptReader().askForConcept("Enter new concept expression: ");
	}
	
	void interactiveExpansion() {
		Tableau tableau = new Tableau();
		tableau.init(new TBOX(defs), concept);
		
		while(true) {
			println("");
			println("TABLEAU:");
			println(tableau.getABOX().toStringRecursive());
			println("");
			Map<ABOX, List<ConceptInstance>> m = tableau.getAvailableExpansions();
			if(m.isEmpty()) {
				println("No more available expansion choices.");
				break;
			}
			println("Available expansion choices:");
			for(ABOX abox : m.keySet()) {
				List<ConceptInstance> lci = m.get(abox);
				for(int i = 0; i < lci.size(); i++) {
					println(abox.getId() + "-" + i + ") " + lci.get(i));
				}
			}
			ABOXConceptSpecReader r = new ABOXConceptSpecReader(tableau, m);
			r.askForConceptInstance("Your choice: ");
			boolean expanded = tableau.expandStep(r.getConceptInstance(), r.getABOX());
			if(!expanded) {
				println("WARNING: no expansion made");
			}
		}
	}
}

abstract class TextualInputReader {
	private String lastError;
	
	protected static void println(String message) {
		System.out.println(message);
	}
	
	protected static void print(String message) {
		System.out.print(message);
	}
	
	protected static BufferedReader bufferedReader = null;
	
	public static String readString() {
		if(bufferedReader == null)
			bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			return bufferedReader.readLine();
		} catch(Exception e) {
			return null;
		}
	}
	
	public String askForString(String prompt) {
		while(true) {
			print(prompt);
			String in = readString();
			if(isValidInput(in)) return in;
			println(getErrorMessage());
		}
	}
	
	public abstract boolean isValidInput(String s);
	
	public String getErrorMessage() {
		return lastError;
	}
	
	protected void setLastErrorMessage(String s) {
		lastError = s;
	}
}

class ConceptReader extends TextualInputReader {
	private AbstractNode concept = null;
	
	public boolean isValidInput(String s) {
		try {
			concept = DLLiteParser.parseConceptExpression(s);
			setLastErrorMessage(null);
			return true;
		} catch(ParseException ex) {
			concept = null;
			setLastErrorMessage("Parser exception: " + ex.getMessage());
			return false;
		}
	}
	
	public AbstractNode getConcept() {
		return concept;
	}
	
	public AbstractNode askForConcept(String prompt) {
		askForString(prompt);
		return getConcept();
	}
}

class DefinitionReader extends TextualInputReader {
	private AbstractDefinition def = null;
	
	@Override
	public boolean isValidInput(String s) {
		try {
			def = DLLiteParser.parseDefinition(s);
			setLastErrorMessage(null);
			return true;
		} catch(ParseException ex) {
			def = null;
			setLastErrorMessage("Parser exception: " + ex.getMessage());
			return false;
		}
	}
	
	public AbstractDefinition getDefinition() {
		return def;
	}
	
	public AbstractDefinition askForDefinition(String prompt) {
		askForString(prompt);
		return getDefinition();
	}
}

class IntegerReader extends TextualInputReader {
	private int i;
	
	@Override
	public boolean isValidInput(String s) {
		try {
			i = Integer.parseInt(s);
			setLastErrorMessage(null);
			return true;
		} catch(NumberFormatException ex) {
			setLastErrorMessage(ex.getMessage());
			return false;
		}
	}
	
	public int getInteger() {
		return i;
	}
	
	public int askForInteger(String prompt) {
		askForString(prompt);
		return getInteger();
	}
}

class ConstrainedIntegerReader extends IntegerReader {
	Integer min, max;
	
	public ConstrainedIntegerReader(Integer min, Integer max) {
		if(min == null && max == null)
			throw new IllegalArgumentException("Missing constraint");
	}
	
	String constraintString() {
		if(min == null) return "<=" + max;
		if(max == null) return ">=" + min;
		return ">=" + min + " and <=" + max;
	}
	
	@Override
	public boolean isValidInput(String s) {
		if(!super.isValidInput(s)) return false;
		int i = getInteger();
		if(!(min == null || i >= min) || !(max == null || i <= max)) {
			setLastErrorMessage("Specify a number " + constraintString());
			return false;
		} else {
			setLastErrorMessage(null);
			return true;
		}
	}
}

class ABOXConceptSpecReader extends TextualInputReader {
	private final Tableau tableau;
	private final Map<ABOX, List<ConceptInstance>> m;
	private ABOX abox = null;
	private int conceptIndex = -1;
	private ConceptInstance conceptInstance = null;
	
	public ABOXConceptSpecReader(Tableau t, Map<ABOX, List<ConceptInstance>> m) {
		this.tableau = t;
		this.m = m;
	}
	
	public boolean isValidInput(String s) {
		int splitPoint = -1;
		if((splitPoint = s.indexOf('-')) < 0) {
			setLastErrorMessage("Bad input format.");
			return false;
		}
		try {
			long aboxId = Long.parseLong(s.substring(0, splitPoint));
			abox = tableau.getABOXById(aboxId);
		} catch(NumberFormatException ex) {
			abox = null;
			conceptIndex = -1;
			setLastErrorMessage("Bad input format.");
			return false;
		}
		if(abox == null || !m.containsKey(abox)) {
			abox = null;
			setLastErrorMessage("Bad ABOX id");
			return false;
		}
		try {
			conceptIndex = Integer.parseInt(s.substring(splitPoint + 1));
		} catch(NumberFormatException ex) {
			abox = null;
			conceptIndex = -1;
			setLastErrorMessage("Bad input format.");
			return false;
		}
		List<ConceptInstance> l = m.get(abox);
		if(conceptIndex < 0 || conceptIndex >= l.size()) {
			abox = null;
			conceptIndex = -1;
			setLastErrorMessage("Bad concept index");
			return false;
		}
		setLastErrorMessage(null);
		conceptInstance = l.get(conceptIndex);
		return true;
	}
	
	public ConceptInstance askForConceptInstance(String prompt) {
		askForString(prompt);
		return getConceptInstance();
	}
	
	public ABOX getABOX() {
		return abox;
	}
	
	public int getConceptIndex() {
		return conceptIndex;
	}
	
	public ConceptInstance getConceptInstance() {
		return conceptInstance;
	}
}