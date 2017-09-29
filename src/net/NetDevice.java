package net;



public class NetDevice {

	public static String getDevice(double bitrate) {

		bitrate = bitrate/1024.0;	//kb

		if (bitrate < 0.1) {
			return "100 baud";
		}

		if (bitrate < 0.2) {
			return "200 baud";
		}

		if (bitrate < 0.3) {
			return "300 baud";
		}

		if (bitrate < 1.0) {
			return "1000 baud";
		}

		if (bitrate < 2.4) {
			return "2400 baud";
		}

		if (bitrate < 4.8) {
			return "4800 baud";
		}


		if (bitrate < 9.6) {
			return "9600 baud";
		}

		if (bitrate < 14.4) {
			return "14.4";
		}

		if (bitrate < 28.8) {
			return "28.8";
		}

		if (bitrate < 64.0) {
			return "ISDN";
		}

		if (bitrate < 128.0) {
			return "2*ISDN";
		}

		if (bitrate < 384.0) {
			return "CABLE-384";
		}

		if (bitrate < 512.0) {
			return "CABLE-512";
		}

		if (bitrate < 768.0) {
			return "CABLE-768";
		}

		if (bitrate < 1024.0) {
			return "1Mbit";
		}

		if (bitrate < 2048.0) {
			return "2Mbit";
		}

		if (bitrate < 3072.0) {
			return "3Mbit";
		}

		if (bitrate < 10240.0) {
			return "10Mbit";
		}

		if (bitrate < 102400.0) {
			return "100Mbit";
		}

		if (bitrate < 1048576.0) {
			return "1Gbit";
		}

		return ">1Gbit ";

	}




}