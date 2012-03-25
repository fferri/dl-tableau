package net.sf.dltableau.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DLTableauBean implements IsSerializable {
	public String original;

	public String nnf;
	
	public DLTableauNode root;
	
	public String toHTML() {
		return toHTML(root);
	}
	
	protected String toHTML(DLTableauNode n) {
		StringBuilder sb = new StringBuilder();
		boolean leaf = n.child.isEmpty();
		sb.append("<table align='center' border='0'>");
		sb.append("<tr><td class='tableau' " + (leaf ? "" : "style='border-bottom: 1px solid black;' ") + "colspan='" + n.child.size() + "'>");
		for(String s : n.expr) sb.append(s).append("<br>");
		sb.append("</td></tr><tr><td class='tableau'>");
		boolean first= true;
		for(DLTableauNode n1 : n.child) {
			if(!first) sb.append("</td><td class='tableau' style='border-left: 1px solid black'>");
			first = false;
			sb.append(toHTML(n1));
		}
		sb.append("</td></tr></table>");
		return sb.toString();
	}
}
