package de.fruitfly;

public class Raytracer implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	
	public static Vector trace(Ray ray, Scene scene, int depth) {
		if (depth == 0) return new Vector(0.0, 0.0, 0.0);
		
		double t = Double.MAX_VALUE;
		Sphere obj = null;
		for (Sphere s : scene.objects) {
			double tt;
			
			if ((tt=s.intersects(ray)) > 0.0 && tt < t) {
				t = tt;
				obj = s;
			}
		}
		Vector allLight = new Vector();
		
		if (obj != null) {
			Vector intersection = ray.lerp(t);
			SurfaceSample ss = obj.getSurfSample(intersection);
			Vector n = ss.normal;

			boolean inside = false;
			if (Vector.dot(ray.direction, n) > 0.0) {
				n.scale(-1.0); 
				inside = true;
			}
						
			if (obj.reflectivity > 0.0 || obj.transparency > 0.0) {
				double facingratio = -Vector.dot(ray.direction, n);
				double fresneleffect = Util.mix(Math.pow(1.0 - facingratio, 3.0), 1.0, 0.1); 
				Vector refractionColor = new Vector();
				Vector reflectionColor = new Vector();

				if (obj.reflectivity > 0.0) {
					//Vector ref = Vector.add(ray.direction, Vector.scale(n, 2.0 * Vector.dot(n, ray.direction), null), null);
					Vector ref = Vector.sub(ray.direction, Vector.scale(n, 2 * Vector.dot(ray.direction, n), null), null);
					ref.normalize();
					//ref.scale(-1.0);
					ref.normalize();
					reflectionColor = trace(new Ray(Vector.add(intersection, Vector.scale(ref, 1e-4, null), null), ref), scene, depth-1);
					if (obj.transparency != 0.0) reflectionColor.scale(fresneleffect);
				}
				if (obj.transparency > 0.0) {
					double ior = 1.1;
					double eta = (inside) ? ior : 1 / ior;
					double cosi = -Vector.dot(n, ray.direction);
					double k = 1 - eta * eta * (1 - cosi * cosi);
					Vector refrdir = Vector.add(Vector.scale(ray.direction, eta, null), Vector.scale(n, eta *  cosi - Math.sqrt(k), null), null);
					refrdir.normalize();
					refractionColor = Raytracer.trace(new Ray(Vector.add(intersection, Vector.scale(n, -1e-4, null), null), refrdir), scene, depth - 1).scale((1.0-fresneleffect) * obj.transparency);
				}
				
				Vector.add(allLight, reflectionColor, allLight);
				Vector.add(allLight, refractionColor, allLight);
			}
			else {
							
nextLight:		for (PointLight pl : scene.lights) {
					Vector l = Vector.sub(pl.getOrigin(), intersection, null);
					double lightDistance = l.length();
					
					// shadow ray
					for (Sphere sObj : scene.objects) {
						if (sObj == obj) continue;
						double t_shadow = sObj.intersects(new Ray(intersection, l));
						
						if (t_shadow > 0.0 && t_shadow < lightDistance) continue nextLight;
					}
					
					l.normalize();
					Vector v = Vector.sub(ray.origin, intersection, null).normalize();
					Vector h = Vector.add(l, v, null).normalize();
					
					double nDotH = Vector.dot(n, h);
					double specularIntensity = Math.max(0.0, Math.pow(nDotH, 100.0));
					//double specularIntensity = 0.0;
		
					double falloff = Math.max(0.0, 1.0 - (lightDistance/pl.getDistance()));
					//falloff *= falloff;
					
					double diffuseIntensity = Util.clamp(Vector.dot(l, n));
					Vector diffuseColor = new Vector(obj.getColor());
					diffuseColor.scale(diffuseIntensity * falloff * obj.diffuseIntensity);
					
					Vector specularColor = new Vector(new Vector(1.0, 1.0, 1.0));
					specularColor.scale(specularIntensity * falloff * obj.specularIntensity);
					
					Vector phong = Vector.add(specularColor, diffuseColor, null);
					Vector.add(allLight, diffuseColor, allLight);
					Vector.add(allLight, specularColor, allLight);
				}
			}
			allLight.saturate();
		}
		return allLight;
	}

}
