package net.sf.dltableau.client;

import java.util.List;

import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DLTableauService</code>.
 */
public interface DLTableauServiceAsync {
	void parseConceptString(String s, AsyncCallback<String> callback);
	
	void parseDefinitionString(String s, AsyncCallback<String> callback);

	void solve(String formula, DLTableauOptions options, AsyncCallback<DLTableauBean> callback);

	void syntaxTree(String concept, DLTableauOptions options, AsyncCallback<String> callback);

	void solve(List<String> tboxDefs, String concept, DLTableauOptions options,
			AsyncCallback<DLTableauBean> callback);
}
