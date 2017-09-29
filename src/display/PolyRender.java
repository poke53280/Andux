
package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Image;
import java.awt.Component;
import java.awt.Point;

public class PolyRender extends PointRender {

	protected Polygon p[] = new Polygon[361];
	protected Polygon m[] = new Polygon[361];

	public PolyRender(Component c,
											FrameProvider source,
											FocaProvider focaSource,
											Dimension size) {

		super(c, source, focaSource, size);
		shipSetup();
	}

	protected void shipSetup() {
		for (int degree=0; degree<=360; degree++) {

          int xOut = (int) Math.round(10* Math.cos(degree*Math.PI/180.0) );
          int xOut_short = xOut>>1;

		  int xMissile = xOut>>2;

		  int yOut = (int) Math.round(10* Math.sin(degree*Math.PI/180.0) );
          int yOut_short = yOut>>1;

		  int yMissile = yOut>>2;

          int midBottomX = 10;
          int midBottomY = 10;


   		  //ship
   		  p[degree] = new Polygon();
          p[degree].addPoint(midBottomX + xOut_short +4, midBottomY + yOut_short +6);
          p[degree].addPoint(midBottomX - yOut + 4, midBottomY + xOut +6);
          p[degree].addPoint(midBottomX - xOut_short+4, midBottomY - yOut_short+6);

	      //missile
	      m[degree] = new Polygon();
          m[degree].addPoint(midBottomX + xMissile +4, midBottomY + yMissile +6);
	      m[degree].addPoint(midBottomX - yOut + 4, midBottomY + xOut +6);
		  m[degree].addPoint(midBottomX - xMissile+ 4, midBottomY - yMissile+6);

	   }

	}

	public void draw() {
		Graphics g = backGC;

		g.setColor(Color.white);
		g.fillRect(0,0,xSize, ySize);

		drawShips(g);
		drawRadar(g);

	}


	protected void drawbgShip(Graphics g, int x, int y, int degree) {

		g.setColor(Color.red);
		g.translate(x,y);
		g.drawPolygon(p[degree]);
		g.translate(-x,-y);

	}

	protected void drawShips(Graphics g) {
		g.setColor(Color.red);

		int MAX = source.getArea(frameBuffer);

		Point _p = source.getOffset();
		int offX = _p.x;
		int offY = _p.y;


		for (int i = 0; i < MAX; i++) {

			int degree = frameBuffer[i].imageRef;
			int x = frameBuffer[i].x - offX;
			int y = frameBuffer[i].y - offY;

			setStateColor(frameBuffer[i].state, g);

			g.translate(x,y);

			if(frameBuffer[i].isMissile) {
				g.drawPolygon(m[degree]);
			} else {
				g.drawPolygon(p[degree]);
				g.drawString("" + frameBuffer[i].energy,0,0);
				g.drawString("" + frameBuffer[i].score,0,30);

				if (frameBuffer[i].name != null) g.drawString(frameBuffer[i].name,0,40);


			}
			g.translate(-x,-y);

		}

	}

}