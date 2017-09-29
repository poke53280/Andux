package display;

import java.awt.Dimension;
import java.awt.Rectangle;

import root.WorldSize;


public class BytePainter {


	protected int W = WorldSize.WORLD_WIDTH;
	protected int H = WorldSize.WORLD_HEIGHT;

	protected byte[] screen;

	public BytePainter(byte[] screen) {
		this.screen = screen;
	}

	protected void uniform(Rectangle r, int intensity) {
		checkWithin(r);
		checkIntensity(intensity);

		for (int i = r.x; i < r.x + r.width; i++) {
			for (int j = r.y; j < r.y + r.height; j++) {
				int p = i + W*j;
				screen[p] = (byte) intensity;
			}
		}
	}

	public void clear() {
		for (int i = 0; i < W*H; i ++) {
			screen[i] = (byte) 0xFF;
		}

	}

	public byte[] getData() {
		return screen;
	}

	protected void dither(Rectangle r, int intensity) {
		checkWithin(r);
		checkIntensity(intensity);

		for (int i = r.x; i < r.x + r.width; i++) {
			for (int j = r.y; j < r.y + r.height; j++) {
				int p = i + W*j;

				int fuzz = 0x08 - (int) (0x10 * Math.random());

				int value = fuzz + intensity;

				if (value < 0) value = 0;
				if (value > 255) value = 255;

				screen[p] = (byte) value;
			}
		}

	}


	private void checkIntensity(int intensity) {
		if (intensity < 0 || intensity > 255) {
			throw new IllegalArgumentException("Intensity "
					+ "out of bounds, intensity = " + intensity);
		}
	}

	private void checkWithin(Rectangle r) {

		int x1 = r.x;
		int y1 = r.y;

		int x2 = x1 + r.width;
		int y2 = y1 + r.height;

		if (x1 < 0 || y1 < 0 || x2 > W || y2 > H) {
			throw new IllegalArgumentException("Area outside world, "
					+ r.toString() );
		}
	}

}