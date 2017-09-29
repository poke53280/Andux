
package io.handler;

import world.Item;
import world.Engine;
import world.ItemFactory;

import io.Comparator;
import display.TextLister;

import root.Command;
import root.Factory;

public class ColorUpdate implements InHandler {

	protected ItemFactory f;
	protected ColorMessage c;
	protected MessageSender s;
	protected Comparator comparator;
	protected TextLister l;

   public ColorUpdate(ItemFactory f, MessageSender s, TextLister l, Comparator comp) {
		  this.f = f;
		  this.c = new ColorMessage();
		  this.s = s;
		  this.l = l;
   		  comparator = comp;
   }

	public String desc() {
		return "color";
	}

   public void input(Message m) {

		if (m == null) {
			System.out.println("ColorUpdate:message null");
			return;
		}

		c.setMessage(m);
		int id = c.getID();

		Item i = f.makeSpec(id);	//When item is created here, message
		//shouldn't be updated as beneath. New message may then  be
		//from outside local area.

		if (i != null ) {

			i.setColor(c.getRed(), c.getGreen(), c.getBlue() );

			String name = c.getName();
			if (name == null) {
				System.out.println("ColorUpdate: Incoming name is null");
			} else {
				i.setName(name);
			}

			String newMessage = c.getChatMessage();
			if (i.getMessage().equals(newMessage) ) {
				//
			} else {
			    i.setMessage(newMessage );
				String iName = i.getName();
				if (iName != null) {
					l.add("<" + iName + ">" + newMessage );
				} else {
					l.add("<" + id + ">" + newMessage );
				}
			}

		} else {
			System.out.println("Warning: ColorUpdate.input:"
				+ " Update rejected, id = " + id);
		}
		c.release();

  }

	public void sendSay() {

		Item i = comparator.findSay();
		if (i == null) {
			//System.out.println("ColorUpdate: None");
			return;
		}

		Item j = comparator.makeAuxSpec(i.ID);
		if (j == null) {
				throw new IllegalStateException("Owned item"
						+ "  found in aux-engine, id=" + i.ID);
		}
		//System.out.println("ColorUpdate:id=" + j.ID);
		output(i);
		j.colorTo(i);

	}

  private void output(Item i) {
		c.create();
		c.setID(i.getID() );
		c.setBlue(i.getBlue() );
		c.setGreen(i.getGreen() );
		c.setRed(i.getRed() );
		c.setChatMessage(i.getMessage() );

		c.setName(i.getName() ); //null is allowed

		s.push(c.getMessage() );
		c.forget();
  }

	public void registerCommands(Factory f, String prefix) {
			//
	}

	public void deregisterCommands(Factory f, String prefix) {
			//
	}

public class ColorMessage extends Manipulator {

	protected static final int CHAT_LENGTH = 40;	//in chars
	protected static final int NAME_LENGTH = 10;
	protected static final int ID = 1;
	protected static final int CHAT = 5;


/*
	protected static final int NAME = 2*CHAT_LENGTH + 5;
	protected static final int red = 2*CHAT_LENGTH +
*/

	protected static final int NAME = 2*CHAT_LENGTH + CHAT;

	protected static final int RED = 2*NAME_LENGTH + NAME;

	//protected static final int RED = 2*CHAT_LENGTH + 5;

	protected static final int GREEN = RED + 4;
	protected static final int BLUE = GREEN + 4;
	protected static final int SIZE = BLUE + 4;



	public ColorMessage() {
		super();
    }

	public void setMessage(Message m) {
		if (this.m == null) {
			this.m = m;
			if (m.getSize() != SIZE) {
				System.out.println("ColorMessage(Message): wrong size:"
														+ m.getSize() );
			}
		}
	}

	public void create() {
		if (m == null) {
			m = Message.getInstance();
			m.setByte(Port.PORT, (byte) Port.COLOR);
			m.setSize(SIZE);

			setRed(0);
			setGreen(0);
			setBlue(0);
		} else {
			System.out.println("ColorMessage.createMessage: Message already set");
		}
	}

	public void setID(int id) {
		m.setInteger(ID, id);
	}

	public int getID() {
		return m.getInteger(ID);
	}

	public void setRed(int c) {
		m.setInteger(RED, c);
	}

	public int getRed() {
		return m.getInteger(RED);
	}

	public void setGreen(int c) {
		m.setInteger(GREEN, c);
	}

	public int getGreen() {
		return m.getInteger(GREEN);
	}

	public void setBlue(int c) {
		m.setInteger(BLUE, c);
	}

	public int getBlue() {
		return m.getInteger(BLUE);
	}

	public void setChatMessage(String s) {
		m.setString(CHAT, s,CHAT_LENGTH);
	}

	public String getChatMessage() {
		return m.getString(CHAT);
	}

	public void setName(String s) {
		m.setString(NAME, s,NAME_LENGTH);
	}

	public String getName() {
		return m.getString(NAME);
	}



}

}
