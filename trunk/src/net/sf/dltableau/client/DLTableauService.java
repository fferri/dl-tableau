package net.sf.dltableau.client;

import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("solve")
public interface DLTableauService extends RemoteService {
	DLTableauBean solve(String formula, DLTableauOptions options) throws Exception;
	
	String syntaxTree(String formula, DLTableauOptions options) throws Exception;
}
