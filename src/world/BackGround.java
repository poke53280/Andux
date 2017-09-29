package world;

import root.WorldSize;
import display.BlockPainter;

import root.Command;
import root.Factory;
import root.ParseUtil;


import java.awt.Rectangle;
import java.awt.Dimension;

public class BackGround {

	protected int W = WorldSize.WORLD_WIDTH;
	protected int H = WorldSize.WORLD_HEIGHT;

	protected BlockPainter p;

	protected String prefix;

	public BackGround(String prefix) {
		this.prefix = prefix;

		p = new BlockPainter(new byte[W*H],
							new Dimension(16,16) );

		p.clear();
		p.setAny(800);


	}

	public void registerCommands(Factory f) {
		f.add(prefix + "map", new Map() );
	}

	public byte[] getData() {
		return p.getData();
	}


	/**
	* MAP COMMAND
	*
	*/

	class Map extends Command {


		private int n;
		private int m;
		private int intensity;

		public Map() {
			setUsage("map {n} {m} {intensity}");
		}

		public Command create(String[] args) {
			Command c = new Map();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				boolean ok = p.map(n,m,intensity);
				if (ok) {
					setResult("mapped OK");
				} else {
					setResult("Bad values");
				}

			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length == PAR3 + 1 ) {
				n = ParseUtil.parseInt(args[PAR1] );
				m = ParseUtil.parseInt(args[PAR2] );
				intensity = ParseUtil.parseInt(args[PAR3] );

				if (n != -1 && m != -1 && intensity != -1 ) {
					isValid = true;
				} else {
					isValid = false;
				}
			} else {
				isValid = false;
			}
		}

	}



}