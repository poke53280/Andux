package io.handler;

public class Syncher implements InHandler {

	private long timeCorrection = 0; //REMOTE_TIME = LOCAL_TIME + timeCorrection;
	private boolean isSynched = false;
	private long latency = 0;
	private MessageSender sender = null;

	private SynchMessage s;

	public Syncher(MessageSender se) {
		this.s = new SynchMessage();
		sender = se;

	}

	public long adjust(long time) {
		if (isSynched ) {
        	return (time - timeCorrection);
        } else {
            return System.currentTimeMillis();
        }
	}

	public String desc() {
		return "sync";
	}


	public String state() {
		if(isSynched) {
			return "" + latency + "ms  Tcorr " + timeCorrection + "ms";
		} else {
			return "N/A";
		}
	}

	public long getLag() {
		return latency;
	}


	public void input(Message m) {
		if (m== null) {
			System.out.println("Syncher:message null");
			return;
		}

		s.setMessage(m);

        if (s.isFull()) {

            timeCorrection = -s.getCorr();
            latency = s.getLatency();
            isSynched = true;
			s.release();

        } else {

             s.addTimer(System.currentTimeMillis() );

             if (s.isFull() ) {

                timeCorrection = s.getCorr();
              	latency = s.getLatency();
               	isSynched = true;

             }
			 sender.push(s.getMessage() );
			 s.forget();
       }
	}

	public void initiate() {
		s.create();
		s.addTimer(System.currentTimeMillis() );
		sender.push(s.getMessage() );
		s.forget();
  	}

public class SynchMessage extends Manipulator {

  protected static final int M_A = 1;
  protected static final int M_B = 9;
  protected static final int M_C = 17;
  protected static final int M_D = 25;
  protected static final int SIZE = 33;


   public SynchMessage() {
		super();
   }

	public void create() {
		if (m == null) {
			m = Message.getInstance();
			m.setByte(Port.PORT, (byte) Port.SYNCH);
			m.setLong(M_A, 0L);
			m.setLong(M_B, 0L);
			m.setLong(M_C, 0L);
			m.setLong(M_D, 0L);
			m.setSize(SIZE);
		} else {
			throw new IllegalStateException("Message already set");

		}
	}

	public void setMessage(Message me) {
		if (this.m == null) {
			this.m = me;
			if (me.getSize() != SIZE) {
				throw new IllegalStateException("wrong size:" + me.getSize() );
			}
		}
	}

	private long getTimerA() {
		return m.getLong(M_A);
    }


  private void setTimerA(long t) {
	m.setLong(M_A, t);
  }

  private long getTimerB() {
	return m.getLong(M_B);

  }

  private void setTimerB(long t) {
		m.setLong(M_B, t);
  }

  private long getTimerC() {
	return m.getLong(M_C);

  }

  private void setTimerC(long t) {
	m.setLong(M_C, t);

  }


  private long getTimerD() {
   	return m.getLong(M_D);

  }

  private void setTimerD(long t) {
	m.setLong(M_D, t);
  }

  public boolean addTimer(long t) {
        if (setUnsetTimer(t, M_A) ) {
            return true;
        }

        if (setUnsetTimer(t, M_B) ) {
            return true;
        }

        if (setUnsetTimer(t, M_C) ) {
            return true;
        }

        if (setUnsetTimer(t, M_D) ) {
            return true;
        }

        return false;

  }


  private boolean setUnsetTimer(long t, int timerID) {
    if (timerID == M_A ||
        timerID == M_B ||
        timerID == M_C ||
        timerID == M_D ) {

		long l = m.getLong(timerID);
		if (l == 0) {
			m.setLong(timerID, t);
			return true;
		} else {
			return false;
		}

    } else {
        throw new IllegalStateException("tried to reach timer in none-timer data field");

     }
  }

	public boolean isFull() {
	    if (getTimerA() != 0 && getTimerB() != 0 && getTimerC() != 0 && getTimerD() != 0) {
	        return true;
	    } else {
	        return false;

	    }

	 }


	public long getLatency() {
		if (!isFull() ) {
			throw new IllegalStateException("SynchMessage not full");
		}

		double lag = getTimerC() - getTimerA() + getTimerD() - getTimerB();
        lag = lag/4.0;
		return Math.round(lag);
	}

	public long getCorr() {
		if (!isFull() ) {
			throw new IllegalStateException("SynchMessage not full");
		}

		double corr = getTimerA() + 3.0 * ( getTimerC() - getTimerB() ) - getTimerD();
        corr = corr/4.0;
        return Math.round(corr);
	}


}


}