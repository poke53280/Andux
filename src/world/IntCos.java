package world;


public class IntCos {

    static int[] cos;
    static int[] sin;

    static final double PI2 = Math.PI * 2.0;
    static final double GRAD_DIV_RAD = 180.0/Math.PI;


    static {


     cos = new int[360];

     for (int angle=0; angle<360; angle++) {

        cos[angle] = (int) Math.round(10.0 * Math.cos(angle*Math.PI/180.0) );

     }


     sin = new int[360];

     for (int angle=0; angle<360; angle++) {

        sin[angle] = (int) Math.round(10.0 * Math.sin(angle*Math.PI/180.0) );

     }

    }


    public IntCos () {


    }


    public static int getCos(int angle) {

        if (angle < 0 || angle >= 360)
            angle = 0;

        int value;
        try {
            value = cos[angle];
        } catch(java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("Index exp when angle = " + angle);
            return 0;

        }
        return value;
    }

    public static int getCos(double radians) {
        if (radians < 0.0 || radians >= PI2) {

            radians = 0.0;

        }
       int angle = (int) Math.round(radians * GRAD_DIV_RAD);

       return getCos(angle);


    }

    public static int getSin(int angle) {

        if (angle < 0 || angle >= 360)
            angle = 0;

        int value;
        try {
            value = sin[angle];
        } catch(java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("Index exp when angle = " + angle);
            return 0;

        }
        return value;
    }

    public static int getSin(double radians) {
        if (radians < 0.0 || radians >= PI2) {
            radians = 0.0;

        }
       int angle = (int) Math.round(radians * GRAD_DIV_RAD);

       return getSin(angle);


    }


}
