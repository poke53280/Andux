package world;

//import com.malavida.world.Criteria;

import java.awt.Rectangle;


public class ItemArray {

	//  State:
	//	Always ready to receive new element.
	//	size is never smaller than one.

	// In outside iterations:
	// When in a for loop removing an item, do:
	//removeAt(pos);
	//pos--;
	//index--;
	//
	//
	//When adding an item
	//create();
	//index++;


	private int size;
	private int index;	//Next free spot

	float GROW_FACTOR = 1.3f;

	private Item[] a;

	public ItemArray(int size) {

		if (size < 1) {
			throw new
				IllegalArgumentException("size < 1, size=" + size);
		}

		this.size = size;
		a = new Item[size];

		index = 0;
		fill();
	}

	public Item add() {

		Item i= a[index];
		increase();

		return i;
	}

	public Item search(int id) {

		int pos = getIndex(id);
		if (pos == -1) {
			return null;
		} else {
			return a[pos];
		}

	}


	public Item searchBefore(int id, int outside) {

		int pos = getIndex(id);
		if (pos == -1 || pos >= outside) {
			return null;
		} else {
			return a[pos];
		}



	}


	public Item delete(int id) {
		int pos = getIndex(id);
		Item i = removeAt(pos);

		ItemState s = i.getState();
		if (s.getState() != ItemState.NONE) {
			throw new IllegalStateException("WARN: ItemArray.delete: "
											+ "Repooled item left in state=" + s.desc() );
		}

		return i;

	}

	public Item[] getArray() {
		return a;
	}

	public int size() {
		return index;
	}

	public void list() {

		System.out.println("--IN USE---");

		for (int pos=0;pos < index;pos++) {
			System.out.println("index = " + pos + ", id=" + a[pos].ID);
		}

		System.out.println("--UNUSED---");

		for (int pos=index;pos < size;pos++) {
			System.out.println("index = " + pos + ", id=" + a[pos].ID);
		}

	}

	public void listTo(int max) {

		if(max < 0 || max > index) {
			throw new IllegalArgumentException("max > index || max < 0");
		}

		System.out.println("--BELOW MAX---");

		for (int pos=0;pos < max;pos++) {
			System.out.println("index = " + pos + ", id=" + a[pos].ID);
		}

		System.out.println("--ABOVE MAX---");

		for (int pos=max;pos < index;pos++) {
			System.out.println("index = " + pos + ", id=" + a[pos].ID);
		}

		System.out.println("--UNUSED---");

		for (int pos=index;pos < size;pos++) {
			System.out.println("index = " + pos + ", id=" + a[pos].ID);
		}


	}



	public String status() {

		return "size=" + size+ ", used="
					+ index + ", capacity=" + 100*index/size + "%";

	}

	private void fill() {

		int offset = index;

		for (int pos = index;pos < size;pos++) {

			Item i = new Item(-1);
			a[pos] = i;

		}

	}


	private void increase() {

		index++;
		if (index == size) {
			grow();
		}

		if (index > size) {
			throw new
				IllegalStateException("index > size");
		}

	}

	private void grow() {

		int newSize = 1 + (int) (size * GROW_FACTOR);

		System.out.println("ItemArray.grow: "
						+ size + "->" + newSize);

		Item[] temp = new Item[newSize];

		System.arraycopy(a,0,temp,0,a.length);

		a = temp;
		size = newSize;
		fill();
	}


	private int getIndex(int id) {

		for(int pos = 0; pos < index;pos++) {
			if(a[pos].ID == id) {
				//System.out.println("ItemArray.getIndex:"
				//	+ " Found item with id " + id + " at " + pos);
				return pos;
			}
		}
		return -1;
	}

	private Item getAt(int pos) {

		if (pos < 0 || pos > index -1) {
			return null;
		} else {
			return a[pos];
		}

	}


	public Item removeAt(int pos) {

		if (pos < 0 || pos > index -1) {
			return null;
		} else {

			Item out = a[pos];

			//System.out.println("ItemArray.removeAt:"
			//		+ " Removing element at " + pos + ", id=" + out.ID);

			a[pos] = a[index-1];//Move last element into position of removed element
			a[index-1] = out;	//Move deleted item into that position
							   	//pos=index-1 when removing last element, but it works.

			index--;			//Shrink active array part past deleted item.

			return out;
		}
	}

/*
	//NOT TESTED, BUT SHOULD WORK

	public int sort(Criteria c) {
		int outside = index;
		Item tmp = null;

		for (int pos=0;pos < outside;pos++) {

			if (!c.isTrue(a[pos]) ) {

				outside--;
				tmp = a[pos];
				a[pos] = a[outside];
				a[outside] = tmp;
				pos--;
			}
		}
		return outside;

	}

//---
*/

	public int sort(Rectangle r) {
		int x1 = r.x;
		int y1 = r.y;
		int x2 = x1 + r.width;
		int y2 = y1 + r.height;

		//System.out.println("ItemArray.sort;Looking in  rect (" + x1 + "," + y1 + "),(" + x2 + "," + y2 + ")");
		return sort(x1,y1,x2,y2);
	}



	public int sort(int x1, int y1, int x2, int y2) {

		//Optimalize:
		//Possibly rebuild to do stuff if element is *inside*,
		//or have two versions.


		//Depending on whether an item typically is inside or outside..
		//
		//

		//Have method return a necessary max of sorted values, based on
		//input integer (which may be size of a buffer.)


		int outside = index;
		Item tmp = null;
		int x;
		int y;


		for (int pos=0;pos < outside;pos++) {

			x = a[pos].x;
			y = a[pos].y;

			if (x < x1 || x >= x2 || y < y1 || y >= y2) {

				outside--;
				tmp = a[pos];
				a[pos] = a[outside];
				a[outside] = tmp;
				pos--;
			}
		}
		return outside;
	}

	public Item getAny() {
		int p = (int) Math.round(Math.random()*( index -1) );

		Item i = getAt(p);
		if (i == null) {
			//System.out.println("getAny(); Got null"); //if empty.
		}
		return i;

	}

	public static void main(String[] args) {

		Item i = null;

		ItemArray a = new ItemArray(1);
		//i = a.add();
		//i = a.add();
		//i = a.add();
		//i = a.add();
		//i = a.add();
		a.list();
		a.getAny();
	}

}