
package gfx;

import root.CycleVector;
import root.Factory;
import root.Command;

import java.awt.Color;

public class Desk {

	Screen screen;
	Rasterizer raster;
	Model model;
	Primitives prim;
	Selector sel;
	Mover mov;

	CycleVector apps = new CycleVector();

	SpaceLocation camera = new SpaceLocation();
	double degrees = 0.0;

	Listener l = null;

	public Desk(int w, int h) {
		screen = new Screen(w,h);
		screen.init();
		raster = screen.getRasterizer();
		model = new Model(raster);



		prim = new Primitives(model);

		raster.setPerspective(true);	//MUST be set now.
		raster.setAntiAlias(false);

		l = new Listener(screen.getXOffset(),
						screen.getYOffset() );

		screen.addMouseListener(l);
		screen.addMouseMotionListener(l);
		screen.addKeyListener(l);

		sel = new Selector(l,apps, screen);
		mov = new Mover(l,20f);

		Transformer tr = model.transformer();
		Matrix m = tr.matrix();
		m.unit();

	}

	public void registerCommands(String prefix, Factory f) {
		if (f == null) return;

		f.add(prefix + "status", new Status() );

	}



	public Rasterizer raster() {
		return raster;
	}

	private void drawAxis() {
		model.setColor(Color.darkGray.getRGB());
		prim.addLine(-200,0,0,200,0,0);
		prim.addLine(0,-200,0,0,200,0);
		prim.addLine(0,0,-200,0,0,200);
		model.commit();
	}

	public Model getModel() { return model; }

	public void add(App a, String n, float x, float y) {

		AppComponent ac = new AppComponent(a, n);
		SpaceLocation sl = ac.spaceLoc();

		sl.setPos(x,y,500f);
		sl.setRot(0.0,0.0,0.0);
		apps.addElement(ac);
	}

	public void draw() {

	    screen.repaint();
		raster.clear();

		sel.focus();

		Transformer tr = model.transformer();
		Matrix m = tr.matrix();
		m.unit();

		camera.translate(m);

		if (l.worldRotate() ) {
			camera.setRot(5.0,degrees/2.0,4.0);
			tr.begin();
				camera.rotate(m);
			tr.end();
		}

		degrees += 1.0;
		//Static world
		drawAxis();

		raster.clearMax();
		AppComponent selected = sel.selected();	//May be null

		if (selected == null) {
			//Use drag to move camera
			mov.move(camera);

		} else {
			//Adjust autorotate/static as set in toggle
			SpaceLocation _sl = selected.spaceLoc();
			_sl.setAutoRotate(l.autoRotate() );

		}

		//Apps
		for(int i = 0; i < apps.size(); i++) {

			tr.pushMatrix();

			AppComponent ac = (AppComponent) apps.elementAt(i);
			App a = ac.getApp();
			if (ac == selected) {
				model.setColor(Color.white.getRGB());
				mov.move(ac);	//apply key counters
			} else {
				model.setColor(Color.lightGray.getRGB() );
			}

			SpaceLocation sl = ac.spaceLoc();

			tr.begin();
				sl.translate(m);
			tr.end();

			if (sl.autoRotate() ) {
				sl.setRot(degrees,degrees,degrees);
				tr.begin();
					sl.rotate(m);
				tr.end();
			}
			float z = sl.z();

			a.draw();

			//Directly to screen. Doesn't draw of no lines.
			raster.drawMax(Color.darkGray.getRGB() );

			//Register raster frames in component.
			ScreenLocation screenLoc = ac.screenLoc();
			screenLoc.setBounds(raster);

			raster.clearMax();

			tr.popMatrix();

		}

	}

	class Status extends Command {

		public Status() {
			setUsage("status");
		}

		public Command create(String[] args) {
			Command c = new Status();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				setResult("raster: " + raster.status());
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}


	public static void main (String[] args) {

		Desk t = new Desk(600,400);
		t.add(new App(t.getModel(), "1st"), "app1", 0f,0f);
		t.add(new App(t.getModel(), "2nd"), "app2", 200f,100f);


		while (true) {
			t.draw();
		}
	}

}