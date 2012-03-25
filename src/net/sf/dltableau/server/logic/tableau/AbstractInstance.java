package net.sf.dltableau.server.logic.tableau;

public abstract class AbstractInstance {
	public static String getIndividualString(int individual) {
		return String.format("%c", 'a' + individual);
	}
}
