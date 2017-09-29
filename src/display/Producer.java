
package display;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;

import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

import java.awt.Rectangle;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.Enumeration;

import root.WorldSize;

public class Producer implements ImageProducer {

	protected Random random = new Random();
	protected ColorModel cm;

//	protected ColorModel cm = new MyColorModel();
	protected Hashtable ht = new Hashtable();

	protected Vector v = new Vector();

	private FrameProvider source;

	protected int x;
	protected int y;
	private int W = WorldSize.WORLD_WIDTH;
	private int H = WorldSize.WORLD_HEIGHT;

	//protected int[] rgn;
	protected byte[] rgn;

	public Producer(int x, int y, byte[] a, FrameProvider source) {

		//cm = new DirectColorModel(24, 0x0000FF, 0x00FF00,0xFF0000);//FASTEST DIRECT

		setupIndexColorModel();

		this.x = x;
		this.y = y;
		rgn = a;

		this.source = source;

	}

	protected void setupIndexColorModel(){
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];

		for(int i=0;i<256;i++) {
			r[i] = g[i] = b[i] = (byte)i;		//grayscale
 			//r[i] = (byte) 0;
 			//b[i] = (byte) 0;
 			//g[i] = (byte) i;


		}

		cm = new IndexColorModel(8,256,r,g,b);




	}


	public void startProduction(ImageConsumer ic) {

		addConsumer(ic);

		Vector ve = (Vector) v.clone();

		Enumeration e = ve.elements();
		while (e.hasMoreElements() ) {
			ic = (ImageConsumer) e.nextElement();
			prepareConsumer(ic);
			sendPixels(ic);
			ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);

		}
	}

	protected void prepareConsumer(ImageConsumer ic) {

		ic.setColorModel(cm);
		ic.setDimensions(x,y);
		ic.setProperties(ht);
		ic.setHints(ImageConsumer.RANDOMPIXELORDER);

/*
		int hints = ImageConsumer.TOPDOWNLEFTRIGHT
		| ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS;

		hints &= ~ImageConsumer.RANDOMPIXELORDER;

		ic.setHints(hints);
*/
	}


	protected void sendPixels(ImageConsumer ic) {

			Rectangle r = source.getFocusRectangle();

			int posX = r.x;
			int posY = r.y;

			int width = r.width;
			int height = r.height;

			int offset = W*posY + posX;

			ic.setPixels(0,0,width,height,cm,rgn,offset,W);
	}

	public void addConsumer(ImageConsumer ic) {
		if (!isConsumer(ic) ) {
			v.addElement(ic);
			prepareConsumer(ic);
			System.out.println("ImageProducer: Consumer added, #=" + v.size() );
		}
	}

	public boolean isConsumer(ImageConsumer ic) { return v.indexOf(ic) > -1; }

	public void removeConsumer(ImageConsumer ic) {
		if (isConsumer(ic) ) {
			v.removeElement(ic);
			System.out.println("ImageProducer: Consumer  removed, #=" + v.size() );
		}

	}
	public void requestTopDownLeftRightResend(ImageConsumer ic) {;}

}