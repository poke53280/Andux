
package gfx;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.DirectColorModel;
import java.awt.Frame;


public class Screen extends Frame implements ImageProducer {

	private Image image;
	private ImageConsumer consumer;
    private DirectColorModel model;

	private int AIR = 100;
	private Rasterizer raster;

	public Screen(int w, int h) {
		super();
		setSize(w+AIR,h+AIR);
		raster = new Rasterizer(w,h);
		setVisible(true);
	}

	public void init() {
		setBackground(Color.black);

	    model = new DirectColorModel(32,0x00FF0000,0x000FF00,0x000000FF,0);
        //model = new DirectColorModel(32,0x00FF0000,0x000FF00,0x000000FF);

        image = Toolkit.getDefaultToolkit().createImage(this);
    }

	public Rasterizer getRasterizer() {
		return raster;
	}

    public void repaint() {
        if (consumer!=null) {
            raster.setPixels(model, consumer);
            consumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
        }
        Graphics g = getGraphics();
		g.drawImage(image,getXOffset(),getYOffset() ,null);

    }

	public int getXOffset() {
		return AIR/2;
	}

	public int getYOffset() {
		return AIR/2;
	}


    public synchronized void addConsumer(ImageConsumer ic) {
        consumer = ic;

        int w = raster.getWidth();
        int h = raster.getHeight();

        consumer.setDimensions(w,h);
        consumer.setHints(
				ImageConsumer.TOPDOWNLEFTRIGHT|
        		ImageConsumer.COMPLETESCANLINES|
        		ImageConsumer.SINGLEPASS|
       			ImageConsumer.SINGLEFRAME);

        consumer.setColorModel(model);
    }
    public boolean isConsumer(ImageConsumer ic) { return true; }
    public void removeConsumer(ImageConsumer ic) {}
    public void startProduction(ImageConsumer ic) { addConsumer(ic);}
    public void requestTopDownLeftRightResend(ImageConsumer ic) { }


}
