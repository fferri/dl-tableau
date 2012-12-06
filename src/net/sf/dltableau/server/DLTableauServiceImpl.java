package net.sf.dltableau.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dltableau.client.DLTableauService;
import net.sf.dltableau.server.logic.abox.ABOX;
import net.sf.dltableau.server.logic.abox.AbstractInstance;
import net.sf.dltableau.server.logic.abox.ConceptInstance;
import net.sf.dltableau.server.logic.render.ASTRenderer;
import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.logic.tableau.Tableau;
import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ParseException;
import net.sf.dltableau.server.parser.ast.AbstractDefinition;
import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauInstance;
import net.sf.dltableau.shared.DLTableauNode;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DLTableauServiceImpl extends RemoteServiceServlet implements DLTableauService {
	@Override
	public String parseConceptString(String s) throws Exception {
		try {
			AbstractNode conceptAST = DLLiteParser.parseConceptExpression(s);
			return conceptAST.toString();
		} catch(ParseException ex) {
			throw new RuntimeException("Parse exception: " + ex.getMessage());
		}
	}

	@Override
	public String parseDefinitionString(String s) throws Exception {
		try {
			AbstractNode definitionAST = DLLiteParser.parseDefinition(s);
			return definitionAST.toString();
		} catch(ParseException ex) {
			throw new RuntimeException("Parse exception: " + ex.getMessage());
		}
	}
	
	@Override
	public DLTableauBean solve(List<String> tboxDefs, String concept, DLTableauOptions options) throws Exception {
		return incrSolve(tboxDefs, concept, null, options);
	}
	
	@Override
	public DLTableauBean incrSolve(List<String> tboxDefs, String concept,
			List<String> expansionSequence, DLTableauOptions options) {
		Key logKey = KeyFactory.createKey("log", "log0");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity e = new Entity("formulaLog", logKey);
		e.setProperty("date", new Date());
		e.setProperty("ip", getThreadLocalRequest().getRemoteAddr());
		
		DLTableauBean ret = new DLTableauBean();
		AbstractNode conceptAST;
		TBOX tbox = null;
		List<AbstractDefinition> defs = new ArrayList<AbstractDefinition>();
		if(tboxDefs != null) {
			for(String defStr : tboxDefs) {
				try {
					defs.add(DLLiteParser.parseDefinition(defStr));
				} catch(ParseException ex) {
					throw new RuntimeException("Error in TBOX definition '" + defStr + "':\n\n" + ex.getMessage());
				}
			}
			tbox = new TBOX(defs);
		}
		e.setProperty("formula", defs + "; " + concept);
		try {
			conceptAST = DLLiteParser.parseConceptExpression(concept);
			e.setProperty("ok", true);
			datastore.put(e);
		} catch(ParseException ex) {
			e.setProperty("ok", false);
			datastore.put(e);
			throw new RuntimeException("Error in concept expression:\n\n" + ex.getMessage());
		}

		ret.original = ExpressionRenderer.render(conceptAST, options.isUsingUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		
		ret.nnf = ExpressionRenderer.render(conceptAST, options.isUsingUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		Tableau tableau = new Tableau();
		tableau.init(tbox, conceptAST);
		if(expansionSequence == null) {
			tableau.expand();
		} else {
			for(String expStr : expansionSequence) {
				String f[] = expStr.split("-");
				long aboxId = Long.parseLong(f[0]);
				ABOX abox = tableau.getABOXById(aboxId);
				if(abox == null) throw new RuntimeException("Bad ABOX id: " + aboxId);
				
				/* bug?
				long instanceId = Long.parseLong(f[1]);
				AbstractInstance ai = abox.getInstanceById(instanceId);
				if(ai == null)
					throw new RuntimeException("Bad instance id: " + instanceId + " (is null)");
				if(!(ai instanceof ConceptInstance))
					throw new RuntimeException("Bad instance id: " + instanceId + " (is not a ConceptInstance)");
				ConceptInstance ci = (ConceptInstance)ai;
				*/
				
				int instanceIdx = Integer.parseInt(f[1]);
				List<ConceptInstance> lci = tableau.getAvailableExpansions(abox);
				ConceptInstance ci = lci.get(instanceIdx);
				if(ci == null)
					throw new RuntimeException("Bad instance id: " + instanceIdx + " (is null)");
				
				if(!tableau.expandStep(ci, abox)) {
					throw new RuntimeException("Fatal: failed tableau expansion in sequential mode");
				}
			}
		}
		ret.expansionSequence = expansionSequence;
		ret.root = buildABOXTree(tableau, tableau.getABOX(), options);

		return ret;
	}
	
	private DLTableauNode buildABOXTree(Tableau tableau, ABOX abox, DLTableauOptions options) {
		DLTableauNode n = new DLTableauNode();
		boolean getAboxInstancesRecursively = !options.isUsingCompactBranches();
		// optimization: only show full abox in leaves:
		getAboxInstancesRecursively = getAboxInstancesRecursively && abox.isLeaf();
		List<ConceptInstance> lci = tableau.getAvailableExpansions(abox);
		for(AbstractInstance i : abox.getInstances(getAboxInstancesRecursively)) {
			DLTableauInstance inst = new DLTableauInstance();
			if(i instanceof ConceptInstance) {
				ConceptInstance ci = (ConceptInstance)i;
				//if(tableau.canExpandStep(ci, abox))
				//	inst.id = abox.getId() + "-" + i.getId();
				if(lci.contains(ci))
					inst.id = abox.getId() + "-" + lci.indexOf(ci);
			}
			inst.expr = ExpressionRenderer.render(i, options.isUsingUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
			n.expr.add(inst);
		}
		if(abox.isLeaf() && abox.containsClash()) {
			DLTableauNode clashMarker = new DLTableauNode();
			DLTableauInstance clashMarkerInst = new DLTableauInstance();
			clashMarkerInst.expr = "&#x22c6;";
			clashMarker.expr.add(clashMarkerInst);
			n.child.add(clashMarker);
		} else {
			for(ABOX abox1 : abox.getChildren()) {
				n.child.add(buildABOXTree(tableau, abox1, options));
			}
		}
		return n;
	}
	
	@Override
	public String syntaxTree(String formula, DLTableauOptions options) throws Exception {
		try {
			AbstractNode concept = DLLiteParser.parseConceptExpression(formula);
			//TBOX tbox = r.getTBOX();
			return ASTRenderer.render(concept, options.isUsingUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		} catch(ParseException e) {
			throw new RuntimeException("Parse exception: " + e.getMessage());
		}
	}
}
