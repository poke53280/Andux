package display;

import java.text.DecimalFormat;


public class PercentFormatter {



	public PercentFormatter() {

		//
	}

	public static void main(String[] args) {

		DecimalFormat d = new DecimalFormat();
		d.applyPattern("##0.00");

		String s;

		s = d.format(new Double(3.2323) );
		System.out.println("Found " + s);

		s = d.format(new Double(-71.324232323) );
		System.out.println("Found " + s);

		s = d.format(new Double(0.000032) );
		System.out.println("Found " + s);

		s = d.format(new Double(0.02121) );
		System.out.println("Found " + s);

		s = d.format(new Double(100.0) );
		System.out.println("Found " + s);

		s = d.format(new Double(0.0) );
		System.out.println("Found " + s);




	}


}