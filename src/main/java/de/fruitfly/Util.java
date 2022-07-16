package de.fruitfly;

public class Util {
	public static int colorToInt(Vector c) {
		return 0xff << 24 | (int)(c.x * 255) << 16 | (int)(c.y * 255) << 8 | (int)(c.z * 255) << 0;
	}
	
	public static double clamp(double d) {
		if (d < 0.0) return 0.0;
		if (d > 1.0) return 1.0;
		return d;
	}
	
	public static double saturate(double d) {
		if (d > 1.0) return 1.0;
		return d;
	}
	
	public static double mix(double a, double b, double mix) {
		return b * mix + a * (1.0 - mix);
	}
}
