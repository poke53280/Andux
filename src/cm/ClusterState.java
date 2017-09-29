

package cm;

import statistics.SampleProvider;

public class ClusterState {

		private int size;
		private float coverage;
		private int total;
		private int overheadCount;
		private int goodCount;
		private int accurateCount;
		private float missPercent;
		private int missCount;
		private int perfectCount;
		private float perfAve;
		private float totalAve;
		private long time;

		private int consistentNode;
		private float consistentPercent;

		private int offline;

		private long tick;

		private Density d;


		//Number of dispatched messages of category user and system
		private int userCount;
		private int sysCount;


		public ClusterState(long tick,Density d) {
			this.tick = tick;
			this.time = tick;

			this.d = d;
		}

		public void setTick(long tick) {
			this.tick = tick;
		}


		public void clear() {
			size = 0;
			coverage = 0f;
			total = 0;
			overheadCount = 0;
			goodCount = 0;
			accurateCount = 0;
			missPercent = 0f;
			missCount = 0;
			perfectCount = 0;
			perfAve = 0f;
			totalAve = 0f;
			offline = 0;
			consistentNode = 0;
			consistentPercent = 0f;
			time = tick;

			userCount = 0;
			sysCount = 0;

		}

		public void register(Evaluation e) {
			if (e == null)
				throw new IllegalArgumentException("e is null");


			missCount += e.getMissing();
			overheadCount += e.getOverHead();
			goodCount += e.getGood();
			accurateCount += e.getAccurate();

			if (e.isOffline() ) offline++;

			if (e.isComplete() ) {
				consistentNode++;
			}

			userCount += e.userCount();
			sysCount += e.sysCount();


			size++;
		}


		public void postCalc() {
			if (size == 0) {
				System.out.println("ClusterState.postCalc:no nodes evaluated");
				return;
			}


			perfectCount = missCount + goodCount;
			total = goodCount + overheadCount;
			coverage = d.getCoverage();
			missPercent = 0f;
			if (perfectCount > 0) {
				missPercent = 100f*missCount/perfectCount;
			}

			perfAve     = 1f*perfectCount/size;
			totalAve    = 1f*total/size;

			int inaccurate = goodCount - accurateCount;


			consistentPercent = 100f*consistentNode/size;
		}


		//Number of links in a perfect system
		public int getNominal() {
			return perfectCount/2;
		}


		public SampleProvider coverage() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) coverage;
				}
			};
		}


		public SampleProvider size() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) size;
				}
			};
		}


		public SampleProvider total() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) total/2f;
				}
			};
		}

		public SampleProvider overheadCount() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) overheadCount/2f;
				}
			};
		}

		public SampleProvider goodCount() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) goodCount/2f;
				}
			};
		}

		public SampleProvider accurateCount() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) accurateCount/2f;
				}
			};
		}


		public SampleProvider missCount() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) missCount/2f;
				}
			};
		}

		public SampleProvider missPercent() {
			return new SampleProvider() {
				public float sampleValue() {
					return missPercent;
				}
			};
		}

		public SampleProvider perfectCount() {
			return new SampleProvider() {
				public float sampleValue() {
					return (float) perfectCount/2f;
				}
			};
		}

		public SampleProvider totalAve() {
			return new SampleProvider() {
				public float sampleValue() {
					return totalAve;
				}
			};
		}

		public SampleProvider perfAve() {
			return new SampleProvider() {
				public float sampleValue() {
					return perfAve;
				}
			};
		}

		public SampleProvider completePercent() {
			return new SampleProvider() {
				public float sampleValue() {
					return consistentPercent;
				}
			};
		}


		public boolean isGood() {
			if (size == 0) {
				//System.out.println("Empty system or never eval'd");
				return false;
			}

			if (consistentNode == size) {
				//System.out.println("All nodes consistent nodes: Good system");
				return true;
			} else {
				//System.out.println("There are inconsistent nodes: Bad system");
				return false;
			}
		}


		public String state() {
			StringBuffer b = new StringBuffer(400 );

			b.append("\n" + size + " node(s) evaluated");

			if (offline > 0) {
				b.append("\n--ALERT: " + offline + " node(s) OFFLINE");
			} else {
				b.append("\nAll nodes online (islands not checked)");
			}

			b.append("\nEvaluation age:   " + (tick - time) + " ms");

			if (size == 0) return b.toString();

			b.append("\nmax coverage%     " + coverage);

			b.append("\nLinks:");
			b.append("\n-total:           " + (total/2) );
			b.append("\n-max possible:    " + (size*(size-1)/2) );

			b.append("\n-overhead:        " + (overheadCount/2) );

			b.append("\n-nominal:         " + (perfectCount/2));

			b.append("\nof these:");
			b.append("\n-known:" + (goodCount/2f));
			b.append("\n--accurate:" + (accurateCount/2f) );

			int inaccurate = goodCount - accurateCount;
			b.append("\n--inaccurate:" + (inaccurate/2f));

			b.append("\n-missing:         " + (missCount/2) + " " + missPercent + "%");




			b.append("\nConnections:");
			b.append("\n-total/node:      " + totalAve );
			b.append("\n-nominal/node:    " + perfAve);

			b.append("\nNodes:");
			b.append("\n#complete hood:    " + consistentNode);
			b.append("\n%complete hood:      " + consistentPercent);

			b.append("\ndispatched (system-user):(" + sysCount + "-" + userCount + ")");

			return b.toString();
		}
	}