package net.sf.dltableau.server.logic.render;

public class RenderMode {
	private boolean unicode = false;
	private boolean html = false;
	
	private RenderMode(boolean unicode, boolean HTML) {
		this.unicode = unicode;
		this.html = HTML;
	}
	
	public boolean isUnicode() {
		return unicode;
	}
	
	public boolean isHTML() {
		return html;
	}
	
	public static final RenderMode PLAINTEXT = new RenderMode(false, false);
	public static final RenderMode UNICODE = new RenderMode(true, false);
	public static final RenderMode HTML = new RenderMode(false, true);
}
