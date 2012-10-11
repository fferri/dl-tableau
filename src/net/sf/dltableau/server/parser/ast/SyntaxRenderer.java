package net.sf.dltableau.server.parser.ast;

public class SyntaxRenderer {
	private static boolean useUnicode = false;

	public static boolean isUseUnicode() {
		return useUnicode;
	}

	public static boolean setUseUnicode(boolean useUnicode) {
		boolean oldSetting = SyntaxRenderer.useUnicode;
		SyntaxRenderer.useUnicode = useUnicode;
		return oldSetting;
	}
	
	public static String AND() {
		return (useUnicode) ? "&#x2293;" : " and ";
	}
	
	public static String OR() {
		return (useUnicode) ? "&#x2294;" : " or ";
	}
	
	public static String NOT() {
		return (useUnicode) ? "&not;" : "not ";
	}
	
	public static String FORALL() {
		return (useUnicode) ? "&forall;" : "forall ";
	}
	
	public static String EXISTS() {
		return (useUnicode) ? "&exist;" : "exists ";
	}
	
	public static String SUBSUMED_BY() {
		return (useUnicode) ? "&#x2291;" : " subsumed-by ";
	}
}
