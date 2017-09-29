
package agent;

import java.util.Vector;


//no order. no idea of used and unused spots.

public class GuestBook {

	int MAX = 100;

	Stay[] stays = new Stay[MAX];

	int counter = 0;	//always ready for insert


	GuestBook(long localhome) {
		for(int i = 0; i < MAX; i++) {
			stays[i] = new Stay(localhome);
		}
	}

	Stay get() {
		Stay s = stays[counter];
		counter++;
		counter%=MAX;
		return s;

	}

	public int counter() { return counter; }
	public Stay[] stays() { return stays; }

	String list(int n) {


		StringBuffer b = new StringBuffer(n*20);

		int index = counter;
		for(int i = 0; i < n; i++) {
			index--;
			if (index < 0) index += MAX;

			b.append('\n');
			b.append(stays[index].show());
		}
		return b.toString();
	}


	String list() {
		return list(MAX);
	}

	//Returns stay for agent(sid,id) at hop nr
	public Stay search(long sid, int id, int nr) {
		for(int i = 0; i < MAX; i++) {
			Stay s = stays[i];
			if (s.search(sid,id,nr)) return s;
		}
		return null;
	}

}