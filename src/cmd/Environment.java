
package cmd;

public class Environment {

	protected int itemID;

	protected String path = "/";

	public Environment() {
		itemID = -1;
	}

	public int getItemID() { return itemID; }
	public void setItemID(int i) { itemID = i;}


	public String getPath() { return path; }
	public void setPath(String s) { path = s; }


	public String status() { return "Environmentstatus. Item=" + getItemID(); }

}