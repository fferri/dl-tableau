package net.sf.dltableau.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DLTableauNode implements IsSerializable {
	public List<String> expr = new ArrayList<String>();
	public List<DLTableauNode> child = new ArrayList<DLTableauNode>();
}
