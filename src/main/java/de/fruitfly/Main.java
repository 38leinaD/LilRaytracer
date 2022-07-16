package de.fruitfly;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;


public class Main extends Canvas implements Runnable, MouseListener, MouseMotionListener {
	public BufferedImage screenBuffer;
	public int[] screenPixels;
	public long tick = 0;
	
	public static final int RenderTargetWidth = 640;
	public static final int RenderTargetHeight = 480;
	public static int maxSkippedFrames = 10;
	public static long tickDurationInNanoseconds = (long) (1e9 / 60);
	
	public Scene scene = new Scene();
	
	public Sphere selectedObject = null;
	
	private void init() {
		screenBuffer = new BufferedImage(RenderTargetWidth, RenderTargetHeight, BufferedImage.TYPE_INT_ARGB);
		screenPixels = ((DataBufferInt)screenBuffer.getRaster().getDataBuffer()).getData();	
		createBufferStrategy(2);
		
		for (int i=0; i<screenPixels.length; i++) {
			screenPixels[i] = 0xff000000;
		}

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		scene.objects.clear();
		scene.lights.clear();
		
		scene.objects.add(new Sphere(new Vector(0.0, -1e5-2, 5.0), 1e5, new Vector(1.0, 1.0, 1.0))); // bottom
		//scene.objects.add(new Sphere(new Vector(0.0, 8+1e5 , 0.0), 1e5, new Vector(1.0, 1.0, 1.0))); // top
		scene.objects.add(new Sphere(new Vector(-1e5-4, 0.0, 5.0), 1e5, new Vector(0.8, 0.1, 0.1))); // left
		//scene.objects.add(new Sphere(new Vector(1e5+8, 0.0, 5.0), 1e5, new Vector(0.1, 0.8, 0.1))); // right
		scene.objects.add(new Sphere(new Vector(0.0, 0.0, -20.0 - 1e5), 1e5, new Vector(0.2, 0.8, 0.2))); // front
		
		scene.objects.add(new Sphere(new Vector(1.0, -0.5, -5.0), 1.0, new Vector(1.0, 0.0, 0.0), 1.0, 1.0, 0.0, 0.0));
		scene.objects.add(new Sphere(new Vector(-2.0, 0.0, -7.0), 1.0, new Vector(0.0, 1.0, 0.0), 1.0, 1.0, 0.0, 0.0));
		scene.objects.add(new Sphere(new Vector(0.2, 1.2, 3.0), 2.5, new Vector(0.0, 0.0, 1.0), 1.0, 1.0, 0.0, 0.0));
		scene.objects.add(new Sphere(new Vector(0.2, 1.0, -7.0), 1.0, new Vector(0.0, 0.0, 1.0), 0.0f, 0.0, 1.0, 0.0));
		scene.objects.add(new Sphere(new Vector(-1.0, 0.5, -5.0), 0.6, new Vector(0.0, 0.0, 1.0), 0.0f, 0.0, 0.2, 0.8));

		scene.lights.add(new PointLight(new Vector(8.0, 8.0, 0.0), 100.0));
		scene.lights.add(new PointLight(new Vector(-2.0, 6.0, 0.0), 15.0));
	}


	private void render() {
		double dx = 1.0/RenderTargetWidth;
		double dy = 1.0/RenderTargetHeight;
		double ratio = RenderTargetWidth/(double)RenderTargetHeight;
		
		for (int y=0; y<RenderTargetHeight; y++) {
			for (int x=0; x<RenderTargetWidth; x++) {
				Ray r = Ray.fromPoints(new Vector(0.0, 0.0, 0.0), new Vector(-0.5 + x/(double)RenderTargetWidth + dx/2.0, (-0.5 + y/(double)RenderTargetHeight + dy/2.0)/ratio, -1.0));
				
				Vector color = Raytracer.trace(r, scene, 5);
				if (color != null) screenPixels[(RenderTargetHeight-y-1)*RenderTargetWidth+x] = Util.colorToInt(color);
			}	
		}
	}



	private void tick() {
		
	}

	private void renderInternal() {
		BufferStrategy bs = getBufferStrategy();
		
		this.render();
		
		Graphics g = getBufferStrategy().getDrawGraphics();
		g.drawImage(this.screenBuffer, 0, 0, RenderTargetWidth, RenderTargetHeight, null);
		g.dispose();
		getBufferStrategy().show();
	}


	public void run() {
		// http://www.koonsolo.com/news/dewitters-gameloop/
		long nextTickTime = System.nanoTime();
		long lastStatsSecond = System.nanoTime() / 1000000000;
		int ticksPerSecond = 0;
		int framesPerSecond = 0;
		while (true) {
			
			int ticks = 0;
			while (System.nanoTime() > nextTickTime && ticks < Main.maxSkippedFrames) {
				tick();			
				tick++;
				nextTickTime += Main.tickDurationInNanoseconds;
				ticks++;
				ticksPerSecond++;
			}
			
			renderInternal();
			framesPerSecond++;
			
			long currentStatsSecond = System.nanoTime() / 1000000000;
			if (currentStatsSecond > lastStatsSecond) {
				lastStatsSecond = currentStatsSecond;
				System.out.println("[Stats] fps: " + framesPerSecond + " tps: " + ticksPerSecond);
				framesPerSecond = 0;
				ticksPerSecond = 0;
			}
		}
	}
	/*
	 * smallpt: http://www.kevinbeason.com/smallpt/
	 * smallpt explained: https://docs.google.com/file/d/0B8g97JkuSSBwUENiWTJXeGtTOHFmSm51UC01YWtCZw/edit?usp=drive_web&pli=1
	 * 
	 * rt lessons: http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-1-writing-a-simple-raytracer/how-does-it-work/
	 * overview picture of a rt: http://inst.eecs.berkeley.edu/~cs184/fa09/resources/raytracing.htm
	 * rt diary: http://inst.eecs.berkeley.edu/~cs184/fa09/raytrace_journal.php
	 */
	public static void main(String[] args) {
	
		
//		Sphere s = new Sphere(new Vector(0.0, 0.0, -5.0), 0.1);
//		Ray r = Ray.fromPoints(new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, -1.0));
//		
//		System.out.println(s.intersects(r));
//		
		
		JFrame frame = new JFrame("Lil Raytracer");
		Dimension d = new Dimension(Main.RenderTargetWidth, Main.RenderTargetHeight);
		frame.getContentPane().setPreferredSize(d);
		frame.setResizable(false);
		frame.setSize((int)d.getWidth(), (int)d.getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setLocation(2000, 200);
		
		Main m = new Main();
	
		frame.add(m);
		m.init();
		
		new Thread(m).start();
		
	}


	@Override
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	private int xPosOld = 0;
	private int yPosOld = 0;
	@Override
	public void mousePressed(MouseEvent me) {
		double dx = 1.0/RenderTargetWidth;
		double dy = 1.0/RenderTargetHeight;
		double ratio = RenderTargetWidth/(double)RenderTargetHeight;
		
		Ray r = Ray.fromPoints(new Vector(0.0, 0.0, 0.0), new Vector(-0.5 + me.getX()/(double)RenderTargetWidth + dx/2.0, (-0.5 + (RenderTargetHeight-me.getY())/(double)RenderTargetHeight + dy/2.0)/ratio, -1.0));
				System.out.println(me.getY());
		double t = Double.MAX_VALUE;
		Sphere selected = null;
		for (Sphere s : scene.objects) {
			double tt;
			if ((tt=s.intersects(r)) > 0.0 && tt < t) {
				t = tt;
				selected = s;
			}
		}
		
		if (selected != null) {
			selectedObject = selected;
			xPosOld = me.getX();
			yPosOld = me.getY();
		}
	}


	@Override
	public void mouseReleased(MouseEvent me) {
		selectedObject = null;
		
	}


	@Override
	public void mouseDragged(MouseEvent me) {
		if (selectedObject == null) return;
		
		double dx = 1.0/RenderTargetWidth * (me.getX()-xPosOld);
		double dy = 1.0/RenderTargetHeight * (me.getY()-yPosOld);

		selectedObject.center.x += dx;
		selectedObject.center.y -= dy;

		xPosOld = me.getX();
		yPosOld = me.getY();
	}


	@Override
	public void mouseMoved(MouseEvent me) {

	}
}
