
package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Image;
import java.awt.Component;

import root.WorldSize;

public class DoubleRender extends PolyRender {

    private Image _im = null;
   	private Consumer cns = null;

	private byte[] bgArray;

	private Dimension size;


	public DoubleRender(Component c, FrameProvider source,
						FocaProvider focaSource, Dimension size, byte[] data) {

		super(c,source, focaSource, size);

		bgArray = data;
		this.source = source;
		this.size = size;
	}

	public void init() {
		super.init();

		Producer p = new Producer(size.width, size.height, bgArray, source);
        _im = c.createImage(p);
        cns  = new Consumer(_im);

	}

	public void cleanUp() {
		if (_im != null) {
			_im.flush();
			_im = null;
		}

		super.cleanUp();
	}

	public void draw() {
		cns.execute();
		Graphics g = backGC;

		backUpdate(g);
		drawShips(g);
		drawRadar(g);

	}

	private void backUpdate(Graphics g) {

		if (cns == null) return;
		if (_im != null && cns.isReady() ) {
      			g.drawImage(_im, 0,0, null);
		}
	}
}