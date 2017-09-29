
package io;
import world.Engine;
import world.Item;

public class SynchAux {

	private Engine main;
	private Engine aux;

	public SynchAux(Engine main, Engine aux) {
		this.main = main;
		this.aux = aux;
	}

	public void synch(Item i) {
		synchAux(i.getID() );
	}

	private void synchAux(int id) {

		//move to 'remote-engine' class

		Item i = main.getItem(id);
		if (i == null) {
			System.out.println("Warning: Comparator.synchAux: Item not found in main engine");
			return;
		}

		Item j = aux.makeSpec(id);
		if (j == null) {
			throw new IllegalStateException("Owned item found in aux-engine, id=" + id);
		}

		j.moveTo(i);

	}


}