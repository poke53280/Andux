
package gfx;

public class Chars {

	int MAX = 125;
	C[] data = new C[MAX];

	int ERROR = MAX -1;

	Model m;

	int x0;
	int y0;

	int x;
	int y;

	int LETTER_WIDTH = 18;
	int LETTER_HEIGHT = 27;

	public Chars(Model m) {
		this.m = m;
		x0 = 0;
		y0 = 0;
		x = x0;
		y = y0;
		dataSetup();
	}

	public void draw(int index) {

		C c = null;
		if (index >= MAX || index < 0) {
			c = data[ERROR];
		} else {
			c = data[index];
		}
		c.draw();
	}

	public void draw(String s) {

		if (s == null || s.equals("") ) return;

		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if( c == '\n') {
				CR();
			} else {
				draw(c);
				nextPos();
			}
			count++;
			if (count%10 == 0) m.commit();
		}
	}

	public void draw(float f, int max) {

		//XXX bad result when on E-323 form

		if (f > 0f && f < 0.001f) f = 0f;
		if (f < 0f && f > -0.001f) f = 0f;

		if (f >= 0f) {
			draw(' ');
			nextPos();
			max--;
		}

		int count = 0;
		String s = "" + f;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			draw(c);
			nextPos();
			count++;
			if (count == max) break;
		}
	}


	public void draw(char c) {
		int n = (int) c;
		if (n >= MAX) n = ERROR;

		//System.out.println("index for '" + c + "'[" + (int) c + "]: " + n);
		draw(n);
	}

	public static void main(String[] args) {
		Chars c = new Chars(null);
		c.draw("aA|a");

	}

	private void dataSetup() {

		C error = new C_ERROR();
		for(int i = 0; i < MAX; i++) {
			data[i] = error;
		}
		data[(int) '0'] = new C_0();
		data[(int) '1'] = new C_1();
		data[(int) '2'] = new C_2();
		data[(int) '3'] = new C_3();
		data[(int) '4'] = new C_4();
		data[(int) '5'] = new C_5();
		data[(int) '6'] = new C_6();
		data[(int) '7'] = new C_7();
		data[(int) '8'] = new C_8();
		data[(int) '9'] = new C_9();

		data[(int) 'A'] = new C_A();
		data[(int) 'B'] = new C_B();
		data[(int) 'C'] = new C_C();
		data[(int) 'D'] = new C_D();
		data[(int) 'E'] = new C_E();
		data[(int) 'F'] = new C_F();
		data[(int) 'G'] = new C_G();
		data[(int) 'H'] = new C_H();
		data[(int) 'I'] = new C_I();
		data[(int) 'J'] = new C_J();
		data[(int) 'K'] = new C_K();
		data[(int) 'L'] = new C_L();
		data[(int) 'M'] = new C_M();
		data[(int) 'N'] = new C_N();
		data[(int) 'O'] = new C_O();
		data[(int) 'P'] = new C_P();
		data[(int) 'Q'] = new C_Q();
		data[(int) 'R'] = new C_R();
		data[(int) 'S'] = new C_5();	//same
		data[(int) 'T'] = new C_T();
		data[(int) 'U'] = new C_U();
		data[(int) 'V'] = new C_V();
		data[(int) 'W'] = new C_W();
		data[(int) 'X'] = new C_X();
		data[(int) 'Y'] = new C_Y();
		data[(int) 'Z'] = new C_Z();

		data[(int) ' '] = new C_SPACE();
		data[(int) '-'] = new C_MINUS();
		data[(int) '+'] = new C_PLUS();

		data[(int) '['] = new C_LBRACKET();
		data[(int) ']'] = new C_RBRACKET();

		data[(int) '('] = new C_LPARA();
		data[(int) ')'] = new C_RPARA();

		data[(int) ':'] = new C_COLON();
		data[(int) '.'] = new C_DOT();

		//ALIASES
		data[(int) ','] = data[(int) '.'];	//comma shows as .
		data[(int) '|'] = data[(int) '+'];	//pipe shows as +
		data[(int) '#'] = data[(int) ' '];	//hash shows as space

		for(int i = (int) 'a' ; i <= (int) 'z'; i++) {
			data[i] = data[i-32];
		}


	}


	public void home() {
		x = x0;
		y = y0;
	}


	public void setUpperLeft(int x0, int y0) {
		this.x0 = x0;
		this.y0 = y0;
		home();
	}

	public void nextPos() {
		x += LETTER_WIDTH;
	}

	public void CR() {
		x = x0;
		y += LETTER_HEIGHT;
	}


	private int addA() {
		return m.addVertex(x,y);
	}

	private int addB() {
		return m.addVertex(x+10,y);
	}

	private int addC() {
		return m.addVertex(x,y-10);
	}

	private int addD() {
		return m.addVertex(x+10,y-10);
	}

	private int addE() {
		return m.addVertex(x,y-20);
	}

	private int addF() {
		return m.addVertex(x+10,y-20);
	}

	private int addG() {
		return m.addVertex(x+5,y);
	}

	private int addH() {
		return m.addVertex(x+5,y-20);
	}

	private int addI() {
		return m.addVertex(x,y-16);
	}

	private int addJ() {
		return m.addVertex(x+5,y-10);
	}

	private int addK() {
		return m.addVertex(x+5,y-16);
	}

	private int addL() {
		return m.addVertex(x+22,y-22);
	}

	private int addM() {
		return m.addVertex(x+10,y-7);
	}

	private int addN() {
		return m.addVertex(x+10,y-13);
	}

	private int addO() {
		return m.addVertex(x+7,y);
	}

	private int addP() {
		return m.addVertex(x+10,y-3);
	}

	private int addQ() {
		return m.addVertex(x+10,y-17);
	}

	private int addR() {
		return m.addVertex(x+7,y-20);
	}

	private int addS() {
		return m.addVertex(x+5,y-5);
	}

	private int addC1() {
		return m.addVertex(x+3,y-5);
	}

	private int addC2() {
		return m.addVertex(x+7,y-5);
	}

	private int addC3() {
		return m.addVertex(x+3,y-9);
	}

	private int addC4() {
		return m.addVertex(x+7,y-9);
	}

	private int addC5() {
		return m.addVertex(x+3,y-12);
	}

	private int addC6() {
		return m.addVertex(x+7,y-12);
	}

	private int addC7() {
		return m.addVertex(x+3,y-16);
	}

	private int addC8() {
		return m.addVertex(x+7,y-16);
	}

	private int addC9() {
		return m.addVertex(x+3,y-16);
	}

	private int addC10() {
		return m.addVertex(x+7,y-16);
	}

	private int addC11() {
		return m.addVertex(x+3,y-20);
	}

	private int addC12() {
		return m.addVertex(x+7,y-20);
	}

	private int addT() {
		return m.addVertex(x+5,y-15);
	}

	abstract class C {
		public abstract void draw();
	}

	class C_0 extends C {
		public void draw() {

			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,F);
			m.addLine(E,F);
			m.addLine(B,E);

		}
	}

	class C_1 extends C {
		public void draw() {
			int B = addB();
			int F = addF();
			m.addLine(B,F);
		}
	}

	class C_2 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(B,D);
			m.addLine(D,C);
			m.addLine(C,E);
			m.addLine(E,F);
		}
	}

	class C_3 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(C,D);
			m.addLine(E,F);
			m.addLine(B,F);
		}
	}

	class C_4 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int F = addF();
			m.addLine(A,C);
			m.addLine(B,F);
			m.addLine(C,D);
		}
	}

	class C_5 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(B,A);
			m.addLine(A,C);
			m.addLine(C,D);
			m.addLine(D,F);
			m.addLine(F,E);
		}
	}

	class C_6 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(B,A);
			m.addLine(A,C);
			m.addLine(C,D);
			m.addLine(D,F);
			m.addLine(F,E);
			m.addLine(C,E);
		}
	}

	class C_7 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int F = addF();
			m.addLine(A,B);
			m.addLine(B,F);
		}
	}

	class C_8 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,F);
			m.addLine(C,D);
			m.addLine(E,F);
		}
	}

	class C_9 extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int F = addF();

			m.addLine(A,B);
			m.addLine(A,C);
			m.addLine(B,F);
			m.addLine(C,D);
		}
	}

	class C_ERROR extends C {
		public void draw() {
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(B,F);
			m.addLine(C,E);
			m.addLine(C,D);
		}
	}

	class C_MINUS extends C {
		public void draw() {
			int C = addC();
			int D = addD();
			m.addLine(C,D);
		}
	}

	class C_PLUS extends C {
		public void draw() {
			int C = addC();
			int D = addD();
			int S = addS();
			int T = addT();
			m.addLine(C,D);
			m.addLine(S,T);
		}
	}

	class C_LBRACKET extends C {
		public void draw() {
			int G = addG();
			int B = addB();
			int H = addH();
			int F = addF();
			m.addLine(G,B);
			m.addLine(G,H);
			m.addLine(H,F);
		}
	}

	class C_RBRACKET extends C {
		public void draw() {
			int G = addG();
			int H = addH();
			int A = addA();
			int E = addE();
			m.addLine(A,G);
			m.addLine(G,H);
			m.addLine(H,E);
		}
	}


	class C_LPARA extends C {
		public void draw() {
			int B = addB();
			int S = addS();
			int T = addT();
			int F = addF();
			m.addLine(B,S);
			m.addLine(S,T);
			m.addLine(T,F);
		}
	}

	class C_RPARA extends C {
		public void draw() {
			int A = addA();
			int S = addS();
			int T = addT();
			int E = addE();
			m.addLine(A,S);
			m.addLine(S,T);
			m.addLine(T,E);
		}
	}

	class C_SPACE extends C {
		public void draw() {
			;
		}
	}

	class C_A extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();

			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,F);
			m.addLine(C,D);
		}
	}

	class C_B extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int E = addE();
			int F = addF();
			int M = addM();
			int J = addJ();
			int D = addD();
			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(E,F);
			m.addLine(B,M);
			m.addLine(J,M);
			m.addLine(C,D);
			m.addLine(D,F);
		}
	}

	class C_C extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(E,F);
		}
	}

	class C_D extends C {
		public void draw() {
			int A = addA();
			int O = addO();
			int P = addP();
			int Q = addQ();
			int R = addR();
			int E = addE();
			m.addLine(A,O);
			m.addLine(O,P);
			m.addLine(P,Q);
			m.addLine(Q,R);
			m.addLine(R,E);
			m.addLine(E,A);
		}
	}

	class C_E extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(C,D);
			m.addLine(E,F);
		}
	}

	class C_F extends C {

		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();

			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(C,D);
		}
	}

	class C_G extends C {
		public void draw() {
			int P = addP();
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			int D = addD();
			int J = addJ();
			m.addLine(P,B);
			m.addLine(B,A);
			m.addLine(A,E);
			m.addLine(E,F);
			m.addLine(F,D);
			m.addLine(D,J);
		}
	}

	class C_H extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();
			m.addLine(A,E);
			m.addLine(B,F);
			m.addLine(C,D);
		}
	}

	class C_I extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();

			int G = addG();
			int H = addH();

			m.addLine(A,B);
			m.addLine(E,F);
			m.addLine(G,H);
		}
	}

	class C_J extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int G = addG();
			int H = addH();
			int I = addI();
			m.addLine(A,B);
			m.addLine(G,H);
			m.addLine(H,I);
		}
	}


	class C_K extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int E = addE();
			int F = addF();
			m.addLine(A,E);
			m.addLine(C,B);
			m.addLine(C,F);
		}
	}

	class C_L extends C {
		public void draw() {
			int A = addA();
			int E = addE();
			int F = addF();
			m.addLine(A,E);
			m.addLine(E,F);
		}
	}

	class C_M extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			int J = addJ();
			m.addLine(E,A);
			m.addLine(A,J);
			m.addLine(J,B);
			m.addLine(B,F);
		}
	}

	class C_N extends C {
		public void draw() {
			int A = addA();
			int E = addE();
			int F = addF();
			int B = addB();
			m.addLine(A,E);
			m.addLine(A,F);
			m.addLine(F,B);
		}
	}

	class C_O extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();

			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,F);
			m.addLine(E,F);

		}
	}

	class C_P extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();

			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,D);
			m.addLine(C,D);
		}
	}

	class C_Q extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			int K = addK();
			int L = addL();

			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,F);
			m.addLine(E,F);
			m.addLine(K,L);
		}
	}


	class C_R extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int C = addC();
			int D = addD();
			int E = addE();
			int F = addF();

			m.addLine(A,B);
			m.addLine(A,E);
			m.addLine(B,D);
			m.addLine(C,D);
			m.addLine(C,F);

		}
	}


	class C_T extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int G = addG();
			int H = addH();

			m.addLine(A,B);
			m.addLine(G,H);
		}
	}


	class C_U extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			m.addLine(A,E);
			m.addLine(E,F);
			m.addLine(F,B);
		}
	}

	class C_V extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int H = addH();

			m.addLine(A,H);
			m.addLine(B,H);
		}
	}

	class C_W extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			int J = addJ();

			m.addLine(A,E);
			m.addLine(E,J);
			m.addLine(J,F);
			m.addLine(F,B);
		}
	}

	class C_X extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			m.addLine(A,F);
			m.addLine(B,E);
		}
	}

	class C_Y extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int J = addJ();
			int H = addH();
			m.addLine(A,J);
			m.addLine(B,J);
			m.addLine(J,H);
		}
	}

	class C_Z extends C {
		public void draw() {
			int A = addA();
			int B = addB();
			int E = addE();
			int F = addF();
			m.addLine(A,B);
			m.addLine(B,E);
			m.addLine(E,F);
		}
	}

	class C_COLON extends C {
		public void draw() {
			int C1 = addC1();
			int C2 = addC2();
			int C3 = addC3();
			int C4 = addC4();
			m.addLine(C1,C2);
			m.addLine(C2,C4);
			m.addLine(C4,C3);
			m.addLine(C3,C1);
			int C5 = addC5();
			int C6 = addC6();
			int C7 = addC7();
			int C8 = addC8();
			m.addLine(C5,C6);
			m.addLine(C6,C8);
			m.addLine(C8,C7);
			m.addLine(C7,C5);
		}
	}

	class C_DOT extends C {
		public void draw() {
			int C9 = addC9();
			int C10 = addC10();
			int C11 = addC11();
			int C12 = addC12();
			m.addLine(C9,C10);
			m.addLine(C10,C12);
			m.addLine(C12,C11);
			m.addLine(C11,C9);
		}
	}


	class C_SHIP extends C {

		public void draw() {
			int E = addE();
			int F = addF();
			int G = addG();
			m.addLine(E,F);
			m.addLine(F,G);
			m.addLine(G,E);
		}
	}

	class C_BOX extends C {

		public void draw() {
			int E = addE();
			int F = addF();
			int D = addD();
			int C = addC();
			m.addLine(E,F);
			m.addLine(F,D);
			m.addLine(D,C);
			m.addLine(C,E);
		}
	}


}