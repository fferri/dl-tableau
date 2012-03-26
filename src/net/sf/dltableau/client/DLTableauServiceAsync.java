package net.sf.dltableau.client;

import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DLTableauService</code>.
 */
public interface DLTableauServiceAsync {
	void solve(String formula, DLTableauOptions options, AsyncCallback<DLTableauBean> callback);

	void syntaxTree(String formula, DLTableauOptions options, AsyncCallback<String> callback);
}
