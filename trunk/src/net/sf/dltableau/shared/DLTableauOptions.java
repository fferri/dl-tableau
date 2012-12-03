package net.sf.dltableau.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DLTableauOptions implements IsSerializable {
	private boolean usingUnicodeSymbols = true;
	
	// show the full ABOX in every branch, or only what has been
	// added with respect to parent ABOX?
	private boolean usingCompactBranches = true;

	public boolean isUsingUnicodeSymbols() {
		return usingUnicodeSymbols;
	}

	public void setUsingUnicodeSymbols(boolean usingUnicodeSymbols) {
		this.usingUnicodeSymbols = usingUnicodeSymbols;
	}
	
	public boolean isUsingCompactBranches() {
		return usingCompactBranches;
	}
	
	public void setUsingCompactBranches(boolean usingCompactBranches) {
		this.usingCompactBranches = usingCompactBranches;
	}
}
