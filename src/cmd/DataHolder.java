package cmd;
import java.util.Observable;

public class DataHolder extends Observable {

	String value = null;

	public void setValue(String s) {
		value = s;
		setChanged();
		notifyObservers(value);
	}
}


