package world;

import java.util.Stack;

public class Impact {

	Stack s = new Stack();
	Engine e;

	public Impact(Engine e) {
		this.e = e;
	}

	public void add(Item target, int killedBy) {
		s.push(new KilledItem(target,killedBy) );

	}


	public void kill() {
		while(!s.isEmpty() ) {

			KilledItem k = (KilledItem) s.pop();

			Item i = k.item;
			int killedBy = k.killedBy;

			Item launcher = e.getItem(killedBy);
			if (launcher != null) {
					launcher.hit(i.ID);
				} else {
					System.out.println("Impact: Launching item not found in host");
			}

			i.exile();
			System.out.println("Impact: Target has been exiled");
		}

	}


	class KilledItem {

		public Item item;
		public int killedBy;

		public KilledItem(Item i, int id) {
			this.item = i;
			killedBy = id;
		}

	}


}