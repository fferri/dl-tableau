package net.sf.dltableau.client;

import java.util.List;

import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("solve")
public interface DLTableauService extends RemoteService {
	String parseConceptString(String s) throws Exception;
	
	String parseDefinitionString(String s) throws Exception;
	
	DLTableauBean solve(List<String> tboxDefs, String concept, DLTableauOptions options) throws Exception;
	
	String syntaxTree(String formula, DLTableauOptions options) throws Exception;
	
	DLTableauBean incrSolve(List<String> tboxDefs, String concept, List<String> expansionSequence, DLTableauOptions options) throws Exception;
}
