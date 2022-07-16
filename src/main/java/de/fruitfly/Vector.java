package de.fruitfly;

public class Vector {
	public double x, y, z;
	
	public Vector() {
		this.x = this.y = this.z = 0.0;
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(Vector v) {
		this(v.x, v.y, v.z);
	}
	
	public static Vector add(Vector a, Vector b, Vector out) {
		if (out == null) out = new Vector();
		out.x = a.x + b.x;
		out.y = a.y + b.y;
		out.z = a.z + b.z;
		return out;
	}
	
	public static Vector sub(Vector a, Vector b, Vector out) {
		if (out == null) out = new Vector();
		out.x = a.x - b.x;
		out.y = a.y - b.y;
		out.z = a.z - b.z;
		return out;
	}
	
	public static Vector mul(Vector a, Vector b, Vector out) {
		if (out == null) out = new Vector();
		out.x = a.x * b.x;
		out.y = a.y * b.y;
		out.z = a.z * b.z;
		return out;
	}
	
	public static Vector scale(Vector a, double s, Vector out) {
		if (out == null) out = new Vector();
		out.x = a.x * s;
		out.y = a.y * s;
		out.z = a.z * s;
		return out;
	}
	
	public Vector scale(double s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		return this;
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public Vector normalize() {
		double linv = 1.0/length();
		Vector.scale(this, linv, this);
		return this;
	}
	
	public static double dot(Vector a, Vector b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	public static Vector cross(Vector a, Vector b, Vector out) {
		if (out == a || out == b) throw new RuntimeException("out cannot be same as operands");
		if (out == null) out = new Vector();
		out.x = a.y*b.z-a.z*b.y;
		out.y = a.z*b.x-a.x*b.z;
		out.z = a.x*b.y-a.y*b.x;
		return out;
	}
	
	public Vector saturate() {
		x = Util.saturate(x);
		y = Util.saturate(y);
		z = Util.saturate(z);

		return this;
	}
}
