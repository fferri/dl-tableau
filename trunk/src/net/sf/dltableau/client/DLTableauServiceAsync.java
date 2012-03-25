package net.sf.dltableau.client;

import net.sf.dltableau.shared.DLTableauBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DLTableauService</code>.
 */
public interface DLTableauServiceAsync {
	void solve(String formula, boolean useUnicode, AsyncCallback<DLTableauBean> callback);

	void syntaxTree(String formula, AsyncCallback<String> callback);
}
