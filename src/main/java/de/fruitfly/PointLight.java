package de.fruitfly;

public class PointLight {
	private Vector origin;
	private double distance;
	
	public PointLight(Vector origin, double distance) {
		this.origin = origin;
		this.distance = distance;
	}

	public Vector getOrigin() {
		return origin;
	}

	public double getDistance() {
		return distance;
	}
}
