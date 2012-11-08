package net.sf.dltableau.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DLTableauNode implements IsSerializable {
	public List<DLTableauInstance> expr = new ArrayList<DLTableauInstance>();
	public List<DLTableauNode> child = new ArrayList<DLTableauNode>();
}
