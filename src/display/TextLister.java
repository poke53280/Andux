
package display;

import java.util.Vector;
import java.awt.Graphics;


public class TextLister {

  private Vector list;
  private int size;

  private int nextAt = 0;

  public TextLister (int s) {

    size = s;

    list = new Vector();


  }

	public void clear() {
		list.clear();
		nextAt = 0;

	}


  public void add(String s) {

    if (nextAt == size ) {
        nextAt = 0;
    }

    if (list.size() < size) {
        list.addElement(s);
    } else {
       list.setElementAt(s, nextAt);
    }

    nextAt++;

  }

  public void list() {

    System.out.println(getList() );

  }


  public String getList() {

	  StringBuffer b = new StringBuffer(300);

	  int i;
      for (i = nextAt -1; i >= 0; i--) {
          String s = (String) list.elementAt(i);
          b.append(s);
      	  b.append('\n');
      }

      for (i = (list.size() -1); i >= nextAt; i--) {
          String s = (String) list.elementAt(i);
          b.append(s);
		  b.append('\n');

      }

	  return b.toString();

  }

public String getReverseList() {

	  StringBuffer b = new StringBuffer(300);

	  int i;

		for (i = nextAt; i<= list.size() -1; i++) {

      //for (i = (list.size() -1); i >= nextAt; i--) {
	            String s = (String) list.elementAt(i);
	            b.append(s);
	  		  b.append('\n');

      }

      //for (i = nextAt -1; i >= 0; i--) {

      for (i = 0;i<= nextAt -1;i++) {
      	String s = (String) list.elementAt(i);
          b.append(s);
      	  b.append('\n');
      }


	  return b.toString();

  }







  public void draw(Graphics g, int x, int y) {

    if (list.size() > 0 ) {

        int lineHeight = 11;

        int count = 0;

        int i;
        for (i = nextAt; i<= list.size() -1; i++) {
            String s = (String) list.elementAt(i);
            g.drawString(s, x, y + count * lineHeight);
            count++;

        }


        for (i = 0; i <= nextAt -1; i++) {
            String s = (String) list.elementAt(i);
            g.drawString(s, x, y + count * lineHeight);
            count++;
        }

    }

  }

}