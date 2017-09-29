
package root;


public class AnyTest {


	public static void main (String[] s) {
		modTest();

	/*
		int MAX = 12;

		for (int i = 0; i < 100; i++) {

			//0...MAX-1 both these are good

			//int index = (int) Math.round(Math.random()*(MAX-1 ) );
			int index = (int) (.5 + Math.random()*(MAX-1));
			System.out.println(index);

		}
	*/

	}


	public static void modTest() {

		int MAX = 100;

		int counter = -3;

		int mod = counter%MAX;

		System.out.println("mod = " + mod);



	}

}