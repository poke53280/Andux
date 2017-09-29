
package gfx;

import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;

public class Rasterizer {

	private final int width;
	private final int height;

	private int pixel[];
	private int bg[];

	private int bgColor = 0;

	private float xmin;
	private float ymin;

	private float xmax;
	private float ymax;

	private boolean motionBlur = false;
	private boolean perspective = false;
	private boolean antiAlias = false;

	private int dxmin;
	private int dxmax;
	private int dymin;
	private int dymax;
	private int lines;


	public Rasterizer(int w, int h) {

		this.width = w;
		this.height = h;

		int size = width*height;
		pixel = new int[size];
		bg = new int[size];


		xmin = 0f;
	    ymin = 0f;
	    xmax = (float)(width-1);
	    ymax = (float)(height -1);

		for (int i = 0; i < size; i++) {
			bg[i] = bgColor;
			pixel[i] = bgColor;
		}

		clearMax();
	}

	public void drawMax(int c) {
		if (lines > 0) lineFast(dxmin, dymin, dxmax, dymax, c);
	}

	public int[] getPixels() {
		return pixel;
	}

	public void setMotionBlur(boolean on) {
		motionBlur = on;
	}

	public void setAntiAlias(boolean on) {
		if (antiAlias == on) return; 	//No state change

		antiAlias = on;
		if (antiAlias) {
			xmin = 12f;
		    ymin = 12f;
		    xmax = (float)(width-13);
		    ymax = (float)(height -13);
		} else {
			xmin = 0f;
		    ymin = 0f;
		    xmax = (float)(width-1);
	    	ymax = (float)(height -1);
		}

	}


	public void setPerspective(boolean on) {
		perspective = on;
	}

	public void clear() {

			int mbs = 10;

			int size = width*height;
			if (motionBlur) {

		        int value, masked,red,green,blue;


		        for (int p=0; p<size; p++) {

		           value = pixel[p];

		           masked = value & 0x00ff0000;
		           red = masked >> 16;

		           masked = value & 0x0000ff00;
		           green = masked >> 8;

		           masked = value & 0x000000ff;
		           blue = masked;

		           red = red>110?red-110:0;
		           green = green>110?green-110:0;
		           blue = blue>110?blue-110:0;

		           red<<=16;
	       		   green<<=8;

	      		   pixel[p] =0xff000000 |
	      		   			(red & 0x00ff0000) |
	      		   			(green & 0x0000ff00) |
	      		   			(blue & 0xff);

	            }
	        } else {
				System.arraycopy(bg,0,pixel,0,width*height);
			}

			clearMax();
		}


		public void clearMax() {
			dxmin = width -1;
			dxmax = 0;
			dymin = height-1;
			dymax = 0;
			lines = 0;

		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public String status() {
			StringBuffer b = new StringBuffer(200);
			b.append("#lines = " + lines);
			return b.toString();
		}


		public void push(float x0,float y0,float z0,
							float x1,float y1,float z1,int color) {


			if (!perspective) {
				_drawLine(x0,y0,x1,y1,color);
				return;
			}

			int f = 512;
			z0 += 500f;
			z1 += 500f;
			if (z0 == 0f) z0 = 1f;
			if (z1 == 0f) z1 = 1f;

			x0 =  f*x0/z0;
			x1 =  f*x1/z1;

			y0 = f*y0/z0;
			y1 = f*y1/z1;


/*
			int b = (int) (z0-500);
			b -= 500;
			b /=10;
			b += 128;
			if (b > 255) b = 255;
			if (b < 40 ) b = 40;
			int c1 = (b & 0xFF) | (b & 0xFF) << 8 | (b & 0xFF) << 16 | 0 << 24;
*/
			_drawLine(x0,y0,x1,y1,color);

//			_drawLine(x0,y0,x1,y1,c1);

		}

		private void _drawLine(float xP, float yP, float xQ, float yQ, int color) {

	 		float dx;
	 		float dy;


			//Origo at center of screen, x big to left, y big up
			//Transfer to screen coordinates
			xP += width/2f;
			xQ += width/2f;

			yP = height/2f - yP;
			yQ = height/2f - yQ;



			//Cohen-Sutherland clipping

		   int cP = (xP < xmin ? 8 : 0) | (xP > xmax ? 4 : 0)
				| (yP < ymin ? 2 : 0) | (yP > ymax ? 1 : 0);

			int cQ = (xQ < xmin ? 8 : 0) | (xQ > xmax ? 4 : 0)
				| (yQ < ymin ? 2 : 0) | (yQ > ymax ? 1 : 0);



	       while ((cP | cQ) != 0) {
			 if ((cP & cQ) != 0) return;	//NO DRAW
	         dx = xQ - xP;
	         dy = yQ - yP;
	         if (cP != 0) {
				if ((cP & 8) == 8) {
					yP += (xmin-xP) * dy / dx; xP = xmin;
				} else if ((cP & 4) == 4){
					yP += (xmax-xP) * dy / dx; xP = xmax;
				} else if ((cP & 2) == 2){
					xP += (ymin-yP) * dx / dy; yP = ymin;
				} else if ((cP & 1) == 1){
					xP += (ymax-yP) * dx / dy; yP = ymax;
				}

				cP = (xP < xmin ? 8 : 0) | (xP > xmax ? 4 : 0)
				| (yP < ymin ? 2 : 0) | (yP > ymax ? 1 : 0);

	         } else if (cQ != 0) {
				if ((cQ & 8) == 8){
					yQ += (xmin-xQ) * dy / dx; xQ = xmin;
				} else if ((cQ & 4) == 4){
					yQ += (xmax-xQ) * dy / dx; xQ = xmax;
				} else if ((cQ & 2) == 2){
					xQ += (ymin-yQ) * dx / dy; yQ = ymin;
				} else if ((cQ & 1) == 1){
					xQ += (ymax-yQ) * dx / dy; yQ = ymax;
				}
	            cQ = (xQ < xmin ? 8 : 0) | (xQ > xmax ? 4 : 0)
				| (yQ < ymin ? 2 : 0) | (yQ > ymax ? 1 : 0);

	         }
	      }

		  if (antiAlias) antiLine((int)xP,(int)yP, (int)xQ,(int)yQ);
		  else			 lineFast((int)xP,(int)yP, (int)xQ,(int)yQ, color);

	   }

		//See if one can fit anti aliasing in here by looking at the current fraction
		private void lineFast(int x0, int y0, int x1, int y1, int color) {

			//Pre: clipped line: Will fit screen.
			if (x0 > dxmax) dxmax = x0;
			else if (x0 < dxmin) dxmin = x0;

			if (y0 > dymax) dymax = y0;
			else if (y0 < dymin) dymin = y0;

			if (x1 > dxmax) dxmax = x1;
			else if (x1 < dxmin) dxmin = x1;

			if (y1 > dymax) dymax = y1;
			else if (y1 < dymin) dymin = y1;

			lines++;

	        int dy = y1 - y0;
	        int dx = x1 - x0;
	        int stepx, stepy;

	        if (dy < 0) { dy = -dy;  stepy = -width; } else { stepy = width; }
	        if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }
	        dy <<= 1;
	        dx <<= 1;

	        y0 *= width;
	        y1 *= width;

	        pixel[x0+y0] = color;
	        if (dx > dy) {
	            int fraction = dy - (dx >> 1);
	            while (x0 != x1) {
	                if (fraction >= 0) {
	                    y0 += stepy;
	                    fraction -= dx;
	                }
	                x0 += stepx;
	                fraction += dy;
	                pixel[x0+y0] = color;
	            }
	        } else {
	            int fraction = dx - (dy >> 1);
	            while (y0 != y1) {
	                if (fraction >= 0) {
	                    x0 += stepx;
	                    fraction -= dy;
	                }
	                y0 += stepy;
	                fraction += dx;
	                pixel[x0+y0] = color;
	            }
	        }
	    }


	public void setPixels(ColorModel m, ImageConsumer c) {

		/*
		if(lines == 0) return;


		int scansize = width;
		int xmin = dxmin;
		int ymin = dymin;
		int xmax = dxmax;
		int ymax = dymax;

		int w = xmax - xmin;
		int h = ymax - ymin;

		int offset = xmin + scansize*ymin;


		c.setPixels(xmin,ymin,w,h,m, pixel,offset,scansize);
		*/
		c.setPixels(0,0,width,height,m,pixel,0,width);

	}


	public String showMax() {
		return "(" + dxmin + "," + dymin + ")-(" + dxmax + "," + dymax + ")";
	}

	public int lines() { return lines; }
	public int xmin()  { return dxmin; }
	public int xmax()  { return dxmax; }
	public int ymin()  { return dymin; }
	public int ymax()  { return dymax; }


	private void antiLine(int h1, int v1, int h2, int v2) {
		int x, y, xInc, yInc;
		int dx, dy;
		int swap;

		if (h2 > dxmax) dxmax = h2;
		else if (h2 < dxmin) dxmin = h2;

		if (v2 > dymax) dymax = v2;
		else if (v2 < dymin) dymin = v2;

		if (h1 > dxmax) dxmax = h1;
		else if (h1 < dxmin) dxmin = h1;

		if (v1 > dymax) dymax = v1;
		else if (v1 < dymin) dymin = v1;

		lines++;


		dx = h2 - h1;
		dy = v2 - v1;

		if ( Math.abs( dx ) > Math.abs( dy ) )
	    {
	    if ( dx < 0 )  {
	        dx = -dx;
	        dy = -dy;
	        swap = v2;  v2 = v1;     v1 = swap;
	        swap = h2;  h2 = h1;      h1 = swap;
	        }
	    x = h1 << 16;
	    y = v1 << 16;

	    if (dx == 0) dx = 1;
	    yInc = ( dy * 65536 ) / dx;

	    while ( (x>>16) < h2 ) {

			int b = y/256;
	        int c1 = (b & 0xFF) | (b & 0xFF) << 8 | (b & 0xFF) << 16 | 0 << 24;
	        int c2 = (~b & 0xFF) | (~b & 0xFF) << 8 | (~b & 0xFF) << 16 | 0 << 24;
			pixel[ (x/65536) + width*(y/65536)] = c1;
			pixel[ (x/65536) + width*(-1 +(y/65536))] = c2;

	        x += ( 1 << 16 );
	        y += yInc;
	        }
	    }
	  else
	    {
	    if ( dy < 0 )
	        {
	        dx = -dx;
	        dy = -dy;
	        swap = v2;
	        v2 = v1;
	        v1 = swap;
	        swap = h2;
	        h2 = h1;
	        h1 = swap;
	        }
	    x = h1 << 16;
	    y = v1 << 16;

	    if (dy == 0) dy = 1;
	    xInc = ( dx * 65536 ) / dy;

	    while ( ( y >> 16 ) < v2 ) {

			int b = y/256;
	        int c1 = (b & 0xFF) | (b & 0xFF) << 8 | (b & 0xFF) << 16 | 0 << 24;
	        int c2 = (~b & 0xFF) | (~b & 0xFF) << 8 | (~b & 0xFF) << 16 | 0 << 24;
			pixel[ (x/65536) + width*(y/65536)] = c1;
			pixel[ -1 + x/65536 + width*(y/65536)] = c2;


	        x += xInc;
	        y += ( 1 << 16 );
	        }
    	}
	}


}

/*
Fast, pseudo-antialiasing.

long x,y,xInc,yInc;
long dx,dy;
int swap;

dx = ( inLine->h2 - inLine->h1 );
dy = ( inLine->v2 - inLine->v1 );

if ( ABS( dx ) > ABS( dy ) )
    {
    if ( dx < 0 )
        {
        dx = -dx;
        dy = -dy;
        swap = inLine->v2;
        inLine->v2 = inLine->v1;
        inLine->v1 = swap;
        swap = inLine->h2;
        inLine->h2 = inLine->h1;
        inLine->h1 = swap;
        }
    x = inLine->h1 << 16;
    y = inLine->v1 << 16;
    yInc = ( dy * 65536 ) / dx;

    while ( ( x >> 16 ) < inLine->h2 )
        {
        SetCPixel( x >> 16,y >> 16,GrayColor( y & 0xFFFF ) );
        SetCPixel( x >> 16,( y >> 16 ) + 1,GrayColor( ~y & 0xFFFF ) );
        x += ( 1 << 16 );
        y += yInc;
        }
    }
  else
    {
    if ( dy < 0 )
        {
        dx = -dx;
        dy = -dy;
        swap = inLine->v2;
        inLine->v2 = inLine->v1;
        inLine->v1 = swap;
        swap = inLine->h2;
        inLine->h2 = inLine->h1;
        inLine->h1 = swap;
        }
    x = inLine->h1 << 16;
    y = inLine->v1 << 16;
    xInc = ( dx * 65536 ) / dy;

    while ( ( y >> 16 ) < inLine->v2 )
        {
        SetCPixel( x >> 16,y >> 16,GrayColor( x & 0xFFFF ) );
        SetCPixel( ( x >> 16 ) + 1,( y >> 16 ),GrayColor( ~x & 0xFFFF ) );
        x += xInc;
        y += ( 1 << 16 );
        }
    }
*/