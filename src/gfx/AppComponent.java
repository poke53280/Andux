package gfx;

class AppComponent {
	public final App a;

	private String name;

	private SpaceLocation spaceLoc;
	private ScreenLocation screenLoc;

	public AppComponent(App a, String name) {
		this.a = a;
		this.name = name;
		this.spaceLoc = new SpaceLocation();
		this.screenLoc = new ScreenLocation();
	}

	public App getApp() {
		return a;
	}

	public SpaceLocation spaceLoc() {
		return spaceLoc;
	}

	public ScreenLocation screenLoc() {
		return screenLoc;
	}

	public String name() { return name; }

}