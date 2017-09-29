
package offline;

import java.awt.Point;

public class User {

	protected int uid;
	protected String name;
	protected String password;
	protected int score;
	protected Point pos;
	protected int itemID;		//Used in Andux to indicate which item is user controlled.

	public User(String name, String password, int uid) {
		this.name = name;
		this.password = password;
		this.uid = uid;
		this.score = 50;
		pos = new Point(176,110);

		System.out.println("User created, name=" + name);
	}

	public String status() {
		return "name=" + name + ", uid=" + uid;
	}

	public boolean isCalled(String n) {
		return n.equals(name);
	}

	public void setStats(int x, int y, int score) {
		pos.x = x;
		pos.y = y;
		this.score = score;
		System.out.println("Stats set, x=" + x + ", y=" + y + " , score=" + score);

	}

	public int getUID() {
		return uid;
	}

	public int getScore() {
		return score;
	}

	public int getX() {
		return pos.x;
	}

		public int getY() {
			return pos.y;
	}

	public void setItemID(int id) {
		itemID = id;
	}

	public int getItemID() {
		return itemID;
	}

	public boolean hasPassword(String p) {
		return p.equals(password);
	}

	public String getUsername() {
		return name;
	}

}