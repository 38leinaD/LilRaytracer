package de.fruitfly;

public class Ray {
	public Vector origin, direction;
	
	public Ray(Vector origin, Vector direction) {
		this.origin = origin;
		this.direction = direction;
		this.direction.normalize();
	}

	public static Ray fromPoints(Vector p1, Vector p2) {
		Vector dir = Vector.sub(p2, p1, null);
		return new Ray(new Vector(p1), dir);
	}
	
	public Vector lerp(double t) {
		return new Vector(
			origin.x + t * direction.x,
			origin.y + t * direction.y,
			origin.z + t * direction.z
		);
	}
}
