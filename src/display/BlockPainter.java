package display;

import java.awt.Rectangle;
import java.awt.Dimension;


public class BlockPainter extends BytePainter {


	protected int xCell;
	protected int yCell;

	public BlockPainter(byte[] screen, Dimension block) {
		super(screen);

		xCell = block.width;
		yCell = block.height;
	}

	public boolean map(int n, int m, int intensity) {
		if (checkMap(n,m,intensity) ) {
			Rectangle r = new Rectangle(xCell*n, yCell*m,xCell,yCell);
			//uniform(r,intensity);
			dither(r,intensity);
			return true;
		} else {
			return false;
		}
	}


	public void setAny() {
		int n = (int) ((W - xCell)/xCell * Math.random() );
		int m = (int) ((H - yCell)/yCell * Math.random() );

		//int intensity = (int) (255 * Math.random() );
		int intensity = 0xFF - (int) (0x30 * Math.random() );

		map(n,m,intensity);
	}

	public void setAny(int number) {
		for (int i = 0; i < number; i++) {
			setAny();
		}
	}

	protected boolean checkMap(int n, int m, int intensity) {
		if (intensity < 0 || intensity > 255) {
			System.out.println("BlockPainter:Bad intensity=" + intensity);
			return false;
		}

		if (n*xCell < 0 || n*xCell >= W-xCell) {
			System.out.println("BlockPainter:Bad x offset: " + n*xCell);
			return false;
		}

		if (m*yCell < 0 || m*yCell >= H-yCell) {
			System.out.println("BlockPainter:Bad y offset: " + m*yCell);
			return false;
		}

		return true;

	}


	public void fill() {
		for (int n = 0; n < W - xCell; n+=xCell) {
			for (int m = 0; m < H - yCell; m+=yCell) {
				int intensity = 0xFF - (int) (0x30 * Math.random() );
				dither(new Rectangle(n,m,xCell,yCell), intensity);
			}
		}
	}


}