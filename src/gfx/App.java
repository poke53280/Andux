
package gfx;


import java.awt.Color;
import java.awt.Component;

public class App {

	protected Model model;
	double degrees = 0.0;

	private Primitives prim;
	protected Chars chars;

	private String msg = null;

	public App (Model m) {
		this(m,"noname");
	}

	public App( Model m, String msg) {
		model = m;
		prim = new Primitives(model);
		chars = new Chars(model);

		this.msg = msg;
	}

	//Called when going active
	public void active(Component c) {
		//System.out.println("   App set active");
	}

	//Called when going inactive
	public void inActive(Component c) {
		//System.out.println("   App set inactive");
	}

	protected void drawAxis() {
		prim.addLine(-50,0,0,50,0,0);
		prim.addLine(0,-50,0,0,50,0);
		prim.addLine(0,0,-50,0,0,50);
		model.commit();
	}

	public void draw() {

		Transformer tr = model.transformer();

		Matrix m = tr.matrix();

		degrees += 1.0;
		degrees %= 360.0;

		tr.pushMatrix();

		drawAxis();
//---
/*
		tr.begin();
				//no change
		tr.end();
*/
		prim.addSquare(200);

		chars.setUpperLeft(0,0);
		chars.draw(msg);

		model.commit();

//---


		tr.begin();
			//m.translate(0f,0f,0f);		//set up rel mov
			m.zrot(degrees);				//	..
			m.yrot(2*degrees);				//	..
			m.xrot(2.3*degrees);
		tr.end();							//pop and apply to state
		prim.addSquare(70);					//deploy
		model.commit();						//flush to raster

//---

		tr.begin();
			m.translate(100f,0f,0f);
			m.zrot(degrees);
		tr.end();
		prim.addSquare(40);
		//chars.home();
		//chars.draw("STATIC");
		model.commit();

//---

		tr.begin();
			m.translate(50f,0f,0f);
			m.zrot(2.0*degrees);
		tr.end();
		prim.addSquare(20);
		model.commit();

//---

		tr.begin();
			m.translate(20f,0f,0f);
			m.zrot(4.0*degrees);
		tr.end();
		prim.addSquare(10);
		model.commit();

//----
		tr.begin();
			m.translate(10f,0f,0f);
			m.zrot(7.0*degrees);
		tr.end();
		prim.addSquare(5);
		model.commit();



//---
		tr.popMatrix();

	}


}