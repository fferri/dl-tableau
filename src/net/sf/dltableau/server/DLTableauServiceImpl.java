package net.sf.dltableau.server;

import java.util.Date;

import net.sf.dltableau.client.DLTableauService;
import net.sf.dltableau.server.logic.abox.ABOX;
import net.sf.dltableau.server.logic.abox.AbstractInstance;
import net.sf.dltableau.server.logic.render.ASTRenderer;
import net.sf.dltableau.server.logic.render.ExpressionRenderer;
import net.sf.dltableau.server.logic.render.RenderMode;
import net.sf.dltableau.server.logic.tableau.Tableau;
import net.sf.dltableau.server.logic.tbox.TBOX;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.DLLiteParser.DLLiteParseResult;
import net.sf.dltableau.server.parser.ParseException;
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
	public DLTableauBean solve(String formula, DLTableauOptions options) throws Exception {
		Key logKey = KeyFactory.createKey("log", "log0");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity e = new Entity("formulaLog", logKey);
		e.setProperty("date", new Date());
		e.setProperty("ip", getThreadLocalRequest().getRemoteAddr());
		
		DLTableauBean ret = new DLTableauBean();
		AbstractNode concept;
		TBOX tbox;
		try {
			DLLiteParseResult r = DLLiteParser.parse(formula);
			concept = r.getFormula();
			tbox = r.getTBOX();
			e.setProperty("formula", formula);
			e.setProperty("ok", true);
			datastore.put(e);
		} catch(ParseException ex) {
			e.setProperty("formula", formula);
			e.setProperty("ok", false);
			datastore.put(e);
			throw new RuntimeException("Parse exception: " + ex.getMessage());
		}

		ret.original = ExpressionRenderer.render(concept, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		
		ret.nnf = ExpressionRenderer.render(concept, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		Tableau tableau = new Tableau();
		tableau.init(tbox, concept);
		tableau.expand();
		ret.root = buildABOXTree(tableau.getABOX(), options);

		return ret;
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
			DLLiteParseResult r = DLLiteParser.parse(formula);
			AbstractNode concept = r.getFormula();
			//TBOX tbox = r.getTBOX();
			return ASTRenderer.render(concept, options.isUseUnicodeSymbols() ? RenderMode.HTML : RenderMode.PLAINTEXT);
		} catch(ParseException e) {
			throw new RuntimeException("Parse exception: " + e.getMessage());
		}
	}
}
