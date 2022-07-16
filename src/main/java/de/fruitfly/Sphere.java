package de.fruitfly;


public class Sphere {
	public Vector center;
	public double radius;
	public Vector color;
	public double reflectivity, transparency;
	public double specularIntensity = 0.0, diffuseIntensity = 1.0;
	
	public Sphere(Vector center, double radius, Vector color) {
		this.center = center;
		this.radius = radius;
		this.color = color;
	}
	
	public Sphere(Vector center, double radius, Vector color, double specularIntensity, double diffuseIntensity, double reflectivity, double transparency) {
		this(center, radius, color);
		this.reflectivity = reflectivity;
		this.transparency = transparency;
		this.specularIntensity = specularIntensity;
		this.diffuseIntensity = diffuseIntensity;
	}

	public double intersects(Ray r) {
		 Vector op = Vector.sub(r.origin, center, null);
		 double eps = 1e-4;
		 // a is 1 because direction of ray is assumed to be normalized
		 double a = 1;
		 double b = 2*Vector.dot(op, r.direction);
		 double c = Vector.dot(op, op) - radius*radius;
		 double det = b*b-4*a*c;
		 if (det < 0) {
			 return 0;
		 }
		 else {
			 det = Math.sqrt(det);
		 }
		 double t = (-b-det)/(2*a);
		 if (t > eps) {
			 return t;
		 }
		 else {
			 t = (-b+det)/(2*a);
			 if (t > eps) {
				 return t;
			 }
			 else {
				 return 0;
			 }
		 }
	}
	
	public SurfaceSample getSurfSample(Vector p) {
		Vector n = Vector.sub(p, center, null);
		n.normalize();
		SurfaceSample s = new SurfaceSample();
		s.normal = n;
		return s;
	}
	
	public Vector getColor() {
		return color;
	}
}
