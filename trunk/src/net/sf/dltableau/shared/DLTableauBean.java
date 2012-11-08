package net.sf.dltableau.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DLTableauBean implements IsSerializable {
	public String original;

	public String nnf;
	
	public DLTableauNode root;
	
	public List<String> expansionSequence;
	
	public String toHTML() {
		return toHTML(root);
	}
	
	protected String toHTML(DLTableauNode n) {
		StringBuilder sb = new StringBuilder();
		boolean leaf = n.child.isEmpty();
		sb.append("<table class='tableau' align='center' border='0'>");
		sb.append("<tr><td class='tableau' " + (leaf ? "" : "style='border-bottom: 1px solid black;' ") + "colspan='" + n.child.size() + "'>");
		for(DLTableauInstance inst : n.expr) {
			if(inst.id != null)
				sb.append("<a class='tce' href=\"#").append(inst.id).append("\">");
			sb.append(inst.expr);
			if(inst.id != null)
				sb.append("</a>");
			sb.append("<br>");
		}
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
