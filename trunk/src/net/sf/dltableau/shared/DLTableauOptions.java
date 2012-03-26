package net.sf.dltableau.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DLTableauOptions implements IsSerializable {
	private boolean useUnicodeSymbols = true;

	public boolean isUseUnicodeSymbols() {
		return useUnicodeSymbols;
	}

	public void setUseUnicodeSymbols(boolean useUnicodeSymbols) {
		this.useUnicodeSymbols = useUnicodeSymbols;
	}
}
