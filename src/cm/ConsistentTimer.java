
package cm;

public class ConsistentTimer {

	boolean consistent = true;

	long stateTime = -1L;							//Time spent in last state
	long stateChanged;	//When state last changed


	long accConsistent = 0L;		//Accumulated time - system consistent
	long accInconsistent = 0L;		//Accumulated time - system inconsistent


	public ConsistentTimer(long tick) {
		stateChanged = tick;
	}


	public void resetAcc() {
		accConsistent = 0L;
		accInconsistent = 0L;
	}

	public long accInconsistent() {
		return accInconsistent;
	}


	public String getState(long tick) {
		StringBuffer b = new StringBuffer(200);

		long dT = tick - stateChanged;
		long t = stateTime;

		long totalConsistent = accConsistent;
		long totalInconsistent = accInconsistent;

		if (consistent) {

			totalConsistent += dT;


			if (t >= 0L) {
				b.append("complete last " + dT + "s. Last incompl.: " + t + "s.");
			} else {
				b.append("complete last " + dT + "s. (Never incompl.)");
			}


		} else {

			totalInconsistent += dT;

			if (t >= 0L) {
				b.append("incomplete last " + dT + "s. Last compl.: " + t + "s.");
			} else {
				throw new IllegalStateException("System never consistent, can't be");
			}
		}
		b.append("\nAcc complete   :" + totalConsistent + " s.");
		b.append("\nAcc incomplete :" + totalInconsistent + " s.");


		if (totalConsistent + totalInconsistent > 0) {
			double tot = (double) (totalConsistent+totalInconsistent);
			float p = 100f * (float) (totalInconsistent/tot);
			b.append("\n%time incomplete: " + p);
		}

		return b.toString();
	}


	//Informs of missing links
	//public void setMissing(int m) {

	//Number of links either missing or known but inaccurate
	public void setInaccurate(int m, long tick) {
		if (m > 0 && consistent) {
			//Change from consistent to inconsistent
			stateTime = tick - stateChanged;
			consistent = false;
			stateChanged = tick;
			//Register the time for the now ended consistent period
			accConsistent += stateTime;
			return;
		}

		if (m > 0 && !consistent) {
			return;
		}

		if (m == 0 && consistent) {
			return;
		}

		if (m == 0 && !consistent) {
			//Change from inconsistent to consistent

			stateTime = tick - stateChanged;
			consistent = true;
			stateChanged = tick;
			accInconsistent += stateTime;

			return;
		}
		throw new IllegalStateException("Unhandled situation");


	}


}