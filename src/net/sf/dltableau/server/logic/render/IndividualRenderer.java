package net.sf.dltableau.server.logic.render;

public class IndividualRenderer {
	public static String render(int individualNumber, RenderMode renderMode) {
		return render("x", individualNumber, renderMode);
	}
	
	public static String render(String base, int individualNumber, RenderMode renderMode) {
		if(renderMode.isUnicode())
			return base + intToStr(individualNumber, unicodeSubscriptDigit);
		if(renderMode.isHTML())
			//return base + "<sub>" + individualNumber + "</sub>";
			return base + intToStr(individualNumber, htmlSubscriptDigit);
		return base + individualNumber;
	}
	
	private static String intToStr(int i, String stringDigits[]) {
		String prefix = "";
		if(i == 0) return stringDigits[0];
		if(i < 0) prefix = "\u208B";
		String result = "";
		int d;
		while(i > 0) {
			d = i % 10;
			result = stringDigits[d] + result;
			i = (i - d) / 10;
		}
		return prefix + result;
	}
	
	private static final String unicodeSubscriptDigit[] = {
		"\u2080", "\u2081", "\u2082", "\u2083", "\u2084",
		"\u2085", "\u2086", "\u2087", "\u2088", "\u2089"
	};
	
	private static final String htmlSubscriptDigit[] = {
		"&#x2080;", "&#x2081;", "&#x2082;", "&#x2083;", "&#x2084;",
		"&#x2085;", "&#x2086;", "&#x2087;", "&#x2088;", "&#x2089;"
	};
}
