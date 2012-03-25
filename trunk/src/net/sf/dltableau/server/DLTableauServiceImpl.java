package net.sf.dltableau.server;

import java.util.Date;

import net.sf.dltableau.client.DLTableauService;
import net.sf.dltableau.server.logic.tableau.ABOX;
import net.sf.dltableau.server.logic.tableau.AbstractInstance;
import net.sf.dltableau.server.logic.tableau.Tableau;
import net.sf.dltableau.server.logic.tableau.Transform;
import net.sf.dltableau.server.parser.DLLiteParser;
import net.sf.dltableau.server.parser.ParseException;
import net.sf.dltableau.server.parser.ast.AbstractNode;
import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauNode;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DLTableauServiceImpl extends RemoteServiceServlet implements DLTableauService {
	@Override
	public DLTableauBean solve(String formula, boolean useUnicode) throws Exception {
		Key logKey = KeyFactory.createKey("log", "log0");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity e = new Entity("formulaLog", logKey);
		e.setProperty("date", new Date());
		e.setProperty("ip", getThreadLocalRequest().getRemoteAddr());
		
		AbstractNode.setUnicodeRendering(false);
		DLTableauBean ret = new DLTableauBean();
		AbstractNode concept, concept0;
		try {
			concept0 = DLLiteParser.parse(formula);
			e.setProperty("formula", concept0.toString());
			e.setProperty("ok", true);
			datastore.put(e);
		} catch(ParseException ex) {
			e.setProperty("formula", formula);
			e.setProperty("ok", false);
			datastore.put(e);
			throw new RuntimeException("Parse exception: " + ex.getMessage());
		}

		AbstractNode.setUnicodeRendering(useUnicode);
		ret.original = concept0.toString();
		
		concept = Transform.pushNotInside(concept0);
		ret.nnf = concept.toString();
		Tableau tableau = new Tableau();
		tableau.init(concept);
		tableau.expand();
		ret.root = buildABOXTree(tableau.abox());

		return ret;
	}
	
	private DLTableauNode buildABOXTree(ABOX abox) {
		DLTableauNode n = new DLTableauNode();
		for(AbstractInstance i : abox.instances()) {
			n.expr.add(i.toString());
		}
		if(abox.isLeaf() && abox.containsClash()) {
			DLTableauNode clashMarker = new DLTableauNode();
			clashMarker.expr.add("&#x22c6;");
			n.child.add(clashMarker);
		} else {
			for(ABOX abox1 : abox.children()) {
				n.child.add(buildABOXTree(abox1));
			}
		}
		return n;
	}
	
	@Override
	public String syntaxTree(String formula) throws Exception {
		AbstractNode concept;
		try {
			concept = DLLiteParser.parse(formula);
		} catch(ParseException e) {
			throw new RuntimeException("Parse exception: " + e.getMessage());
		}
		return concept.treeString();
	}
}
