package display;

import java.awt.Dimension;

import root.WorldSize;
import world.ItemState;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Point;

public class PointRender extends AbstractRender {

	protected int xSize;
	protected int ySize;

	protected Image backBuffer;
	protected Graphics backGC;

	protected RenderImage[] frameBuffer;
	protected FrameProvider source;

	protected Rectangle[] foca;
	protected FocaProvider focaSource;


	protected int MAX;
	protected int FOCAMAX = 0;

	protected int xPos;						//Positions of radar
	protected int yPos;
	protected int RADAR_DIV = 3;					//Radar 1/size of main view
	//protected int RADAR_X_AIR = 20;
	//protected int RADAR_Y_AIR = 20;
	protected int RADAR_X_AIR = 3;
	protected int RADAR_Y_AIR = 3;



	protected int xRSize;					//Radar size
	protected int yRSize;
	protected int xDiv;
	protected int yDiv;


	protected void setBG() {

		backBuffer = c.createImage(xSize, ySize);

		backGC = backBuffer.getGraphics();

		backGC.setColor(Color.white);
		backGC.fillRect(0,0,xSize -1,  ySize -1);

		//Graphics g = getGraphics();

		//setBackground(Color.white);

		//g.setColor(Color.white);
		//g.fillRect(0,0,xSize-1,ySize-1);

	}


	public PointRender(Component c, FrameProvider source, FocaProvider focaSource, Dimension size) {

		super(c);

		xSize = size.width;
		ySize = size.height;

		this.source = source;
		this.focaSource = focaSource;

		frameBuffer = RenderImage.createBuffer(MAX_FB );
		//frameBuffer = new RenderImage[MAX_FB];
		//for (int i = 0; i < MAX_FB; i++) {
		//	frameBuffer[i] = new RenderImage();
		//}

		foca = new Rectangle[MAX_FOCA];

		for (int j = 0; j < MAX_FOCA;j++) {
			foca[j] = new Rectangle();
		}

		setupRadar();

	}


	protected void setupRadar() {

		xRSize = xSize /RADAR_DIV;
		yRSize = ySize /RADAR_DIV;

		Dimension d = WorldSize.WORLD_SIZE;

		xDiv = d.width/xRSize;
		yDiv = d.height/yRSize;

		//Adjusting sizes to allow integer operations:
		xRSize = d.width/xDiv;
		yRSize = d.height/yDiv;

		xPos = xSize - RADAR_X_AIR - xRSize;
		yPos = ySize - RADAR_Y_AIR - yRSize;


	}


	public void init() {
		setBG();
	}


	public void draw() {
		Graphics g = backGC;
		drawShipPoints(g);
		drawRadar(g);
	}


	protected void drawShipPoints(Graphics g) {

		g.setColor(Color.red);
		g.fillRect(0,0,xSize, ySize);

		int MAX = source.getArea(frameBuffer);

		Point p = source.getOffset();
		int offX = p.x;
		int offY = p.y;


		g.setColor(Color.white);
		g.fillRect(0,0,2,2);

		for (int i = 0; i < MAX; i++) {
			//int x = frameBuffer[i].x;
			//int y = frameBuffer[i].y;
			int x = frameBuffer[i].x - offX;
			int y = frameBuffer[i].y - offY;

			g.copyArea(0,0,2,2,x,y);
		}
	}


	protected void drawRadarFrame(Graphics g) {

		g.setColor(Color.black);

		/*
		int l = 15;
		g.drawLine(xPos,yPos,xPos+l,yPos);
		g.drawLine(xPos,yPos,xPos,yPos+l);
		et.c...
		*/


		g.drawRect(xPos,yPos,xRSize+1,yRSize+1);
	}


	protected void drawRadar(Graphics g) {

		g.setColor(Color.white);

		int allX = xSize;
		int allY = ySize;

		MAX = source.getAll(frameBuffer);
		FOCAMAX = focaSource.getFoca(foca);

		g.setColor(Color.red);

		for (int j = 0;j < FOCAMAX;j++) {
			Rectangle r = foca[j];
			int x  = xPos + r.x/xDiv;
			int y  = yPos + r.y/ yDiv;
			int w  = r.width /xDiv;
			int h  = r.height/yDiv;

			g.drawRect(x,y,w,h);

		}

		g.setColor(Color.black);

		Rectangle localFocus = source.getFocusRectangle();
		int locX = xPos + localFocus.x/xDiv;
		int locY = yPos + localFocus.y/ yDiv;
		int locW= localFocus.width/xDiv;
		int locH = localFocus.height/yDiv;

		g.drawRect(locX,locY,locW, locH);


		for (int i = 0; i < MAX; i++) {

			int x = xPos + frameBuffer[i].x / xDiv;
			int y = yPos + frameBuffer[i].y / yDiv;
			int state = frameBuffer[i].state;

			setStateColor(state, g);

			g.fillRect(x,y,2,2);
			//g.drawLine(x,y,x,y);

		}
		drawRadarFrame(g);
	}

	protected void setStateColor(int state, Graphics g) {

		if (state == ItemState.OWN) {
				g.setColor(Color.red);
			}

			if (state == ItemState.TRANSFER) {
				g.setColor(Color.blue);
			}

			if (state == ItemState.SPEC) {
				g.setColor(Color.green);
			}

			if (state == ItemState.DROP) {
				g.setColor(Color.orange);
			}

	}


	public void update(Graphics g) {
	 	if (backBuffer == null) {
			return;
		}

		Rectangle r = getBounds();

		g.translate(r.x,r.y);
		g.drawImage(backBuffer,0,0,null);
		g.translate(-r.x, -r.y);

	}

	public void paint(Graphics g) {
		update(g);
	}

	public void cleanUp() {
		backBuffer.flush();
		backGC = null;
	}

}