package io;

import world.Engine;
import world.ItemArray;
import world.Item;
import world.RotSet;
import world.ItemState;
import world.Focus;

import root.Command;
import root.Factory;
import root.ParseUtil;
import java.awt.Point;

public class Comparator {

	private Engine main;
	private Engine aux;

	private int THRESHOLD = 4;

	public Comparator(Engine main, Engine aux) {
		this.main = main;
		this.aux = aux;
	}

	public Item findSay() {
		//looks up *any* in aux. finds main, checks for newer
		//(other) message in main. returns main item if so is found.

		Item j = getAnyAux();
		if (j == null) {
			//Aux is empty
			return null;
		}

		ItemArray a = main.getData();
		Item i = a.search(j.ID);
		if (i == null) {
			//System.out.println("Comperator.findSay:Item"
			//				+ " in aux, but not in main, id=" + j.ID);
			return null;
		}

		String auxMessage  = j.getMessage();
		String mainMessage = i.getMessage();

		if (auxMessage.equals(mainMessage)) {
			return null;
		} else {
			return i;
		}
	}


	public Item makeAuxSpec(int id) {
		return aux.makeSpec(id);
	}


	public Item getOutside() {

		ItemArray auxData = aux.getData();
		ItemArray mainData= main.getData();

		Focus f = aux.getFocus();

		int out = mainData.sort(f.getRect() );
		Item[] a = mainData.getArray();

		for (int pos=0;pos < out;pos++) {

			Item k = a[pos];
			ItemState s = k.getState();
			if (s.isControlled() ) {

				Item j = auxData.search(k.ID);

				if(isOutside(j,k) ) {
					return k;
				} else {
					//Aux item is properly describing position in main engine.
					//headCheck(j,k);
					if (!isHeaded(j,k) ) {
						return k;
					}


				}
			}
		}
		return null;
	}


	public Item exileObsolete() {

		ItemArray auxData = aux.getData();
		ItemArray mainData= main.getData();

		Focus f = aux.getFocus();
		int out = mainData.sort(f.getRect() );


		Item[] a = auxData.getArray();

		int index = auxData.size();
		for (int pos=0;pos < index;pos++) {

			int id = a[pos].ID;

			Item i = mainData.search(id);

			if (i == null || !i.getState().isControlled() ) {
				//Item is in aux engine, but not in main engine. It should not be in
		       //aux engine, but mother host should also be informed.
		       //Send a new dummy position, far way from anywhere.

				//Recheck; XXXFor now, just delete immediately. Old items may be stuck.

				ItemState s = a[pos].getState();
				s.destroy();
				auxData.removeAt(pos);
				pos--;
				index--;

			} else if (!isOutside(a[pos], i) ){
				//Aux item is proplery describing main engine item.
				//headCheck(a[pos],i);
				if (!isHeaded(a[pos],i) ) {
					return i;
				}


			} else {
				//Aux item is not close enough to main engine item. Aux engine needs to
				//be updated, whether main item is within focus or not.

				return i;
			}
		}
		return null;
	}


	public Item getTransfer() {

		Focus f = aux.getFocus();
		Item i = main.getTransfer(f);
		return i; //null if none found.
	}

	public Item getAnyAux() {
		return aux.getAny();
	}

	private String showDiff(int id) {

		Item l = aux.getItem(id);
		Item m = main.getItem(id);

		if (l == null || m == null) {
			return "Item id=" + id + " not found in main and/or aux engine";
		} else {
			Point p = m.getDiff(l);
			return "Item id=" + id + ": dx = " + p.x + ", dy = " + p.y;
		}

	}

	private boolean isOutside(Item auxItem, Item mainItem) {
		if (mainItem == null) {
			return false;
		}

		if (auxItem == null) {
			return true;
		}

		Point p = Item.getDiff(auxItem, mainItem);

		if (p.x > THRESHOLD || p.y > THRESHOLD) {
			return true;
		} else {
			return false;
		}
	}


	private void headCheck(Item auxItem, Item mainItem) {

		boolean isHeaded = isHeaded(auxItem, mainItem);
		if (!isHeaded) {
			System.out.println("Comparator: Head update needed on " + auxItem.ID);
		} else {
			System.out.println("Heading OK");
		}

	}


	private boolean isHeaded(Item auxItem, Item mainItem) {
		if (mainItem == null) {
				return false;
		}

		if (auxItem == null) {
				return true;
		}

		if (mainItem == auxItem) {
			System.out.println("WARN: Comparator: aux and main items are identical");
		} else {
			//System.out.println("Comparator: aux and main items are NOT identical");
		}

		RotSet jr = auxItem.getRotation();
		RotSet ir = mainItem.getRotation();

		if (jr == ir) {
			System.out.println("WARN: Comparator: aux and main items have identical rotation object");
		}

		return jr.isHeaded(ir);

	}



	public void registerCommands(Factory f, String prefix) {
			f.add(prefix + "c/diff"  , new Diff() );
			f.add(prefix + "c/update", new Update() );
			f.add(prefix + "c/transferscan", new TransferScan() );
			f.add(prefix + "c/obsolete", new Obsolete() );

	}

	public void deregisterCommands(Factory f, String prefix) {
			f.remove(prefix + "c/diff");
			f.remove(prefix + "c/update");
			f.remove(prefix + "c/transferscan");
			f.remove(prefix + "c/obsolete");

	}


	class Obsolete extends Command {

		public Obsolete() {
			setUsage("obsolete");
		}

		public Command create(String[] args) {
			Command c = new Obsolete();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				exileObsolete();
				setResult("Obsolete finished.");
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}


	class TransferScan extends Command {

		public TransferScan() {
			setUsage("transferscan");
		}

		public Command create(String[] args) {
			Command c = new TransferScan();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				Item i = getTransfer();
				if (i != null) {
					setResult("Item ID " + i.getID() + " on TRANSFER and in focus zone.");
				} else {
					setResult("No TRANSFER items in focus zone.");
				}

			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}


class Diff extends Command {

		private int itemID = -1;

		public Diff() {
			setUsage("diff <itemID>");
		}

		public Command create(String[] args) {
			Command c = new Diff();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			itemID = -1;
		}

		public void execute() {
			if (isValid ) {
				setResult(showDiff(itemID) );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length != PAR1 +1) {
				isValid = false;
			} else {
				itemID = ParseUtil.parseInt(args[PAR1] );
				if (itemID== -1 ) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}
	}

	class Update extends Command {

		public Update() {
			setUsage("update");
		}

		public Command create(String[] args) {
			Command c = new Update();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				getOutside();
				setResult("");
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}