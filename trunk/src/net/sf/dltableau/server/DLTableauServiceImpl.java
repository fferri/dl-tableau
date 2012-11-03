package net.sf.dltableau.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dltableau.client.DLTableauService;
import net.sf.dltableau.server.logic.abox.ABOX;
import net.sf.dltableau.server.logic.abox.AbstractInstance;
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

		ret.original = ExpressionRenderer.render(conceptAST, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		
		ret.nnf = ExpressionRenderer.render(conceptAST, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		Tableau tableau = new Tableau();
		tableau.init(tbox, conceptAST);
		tableau.expand();
		ret.root = buildABOXTree(tableau.getABOX(), options);

		return ret;
	}
	
	@Override
	public DLTableauBean solve(String concept, DLTableauOptions options) throws Exception {
		return solve(null, concept, options);
	}
	
	private DLTableauNode buildABOXTree(ABOX abox, DLTableauOptions options) {
		DLTableauNode n = new DLTableauNode();
		for(AbstractInstance i : abox.getInstances(false)) {
			n.expr.add(ExpressionRenderer.render(i, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT));
		}
		if(abox.isLeaf() && abox.containsClash()) {
			DLTableauNode clashMarker = new DLTableauNode();
			clashMarker.expr.add("&#x22c6;");
			n.child.add(clashMarker);
		} else {
			for(ABOX abox1 : abox.getChildren()) {
				n.child.add(buildABOXTree(abox1, options));
			}
		}
		return n;
	}
	
	@Override
	public String syntaxTree(String formula, DLTableauOptions options) throws Exception {
		try {
			AbstractNode concept = DLLiteParser.parseConceptExpression(formula);
			//TBOX tbox = r.getTBOX();
			return ASTRenderer.render(concept, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		} catch(ParseException e) {
			throw new RuntimeException("Parse exception: " + e.getMessage());
		}
	}
}
