package display;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Consumer implements ImageConsumer {

   private ImageProducer ip;

   private boolean imageReady = false;

	public Consumer(Image i) {
		ip = i.getSource();
	}


  public boolean isReady() {
	  return imageReady;
  }

   public void imageComplete(int nStatus)  {
		if (nStatus != 2) {
			System.out.println("Consumer status " + nStatus);
		}

		imageReady = true;
   }

   public void execute()  {
     	imageReady = false;
    	 ip.startProduction(this);
   }

   public void setColorModel(ColorModel cm)  {
      ;
   }

   public void setDimensions(int w, int h)  {
      ;
   }

   public void setHints(int nHints)  {
      ;
   }

   public void setProperties(Hashtable htProperties)  {
      ;
   }

   public void setPixels(int x, int y, int w, int h,
                         ColorModel cm, byte rgb[],
                         int nOffset, int nScansize)
   {
      ;
   }

   public void setPixels(int x, int y, int w, int h,
                         ColorModel cm, int rgn[],
                         int nOffset, int nScansize)
   {
      ;
   }
}

