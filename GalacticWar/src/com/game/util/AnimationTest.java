package com.game.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Random;

import javax.swing.JFrame;

public class AnimationTest extends JFrame implements Runnable {
	int scrWidth = 640;
	int scrHeight = 480;
	
	BufferedImage backbuffer;
	Graphics2D g2d;
	
	Thread gameloop;
	Random rand = new Random();
	
	Image image;
	Point pos = new Point(300,200);
	
	int currentFrame = 0;
	int totalFrames = 30;
	int animationDirection = 1;
	int frameCount = 0;
	int frameDelay = 10;
	
	public static void main(String[] args) {
		new AnimationTest();
	}

	public AnimationTest() {
		super("Animation Test");
		setSize(scrWidth, scrHeight);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		backbuffer = new BufferedImage(scrWidth, scrHeight, BufferedImage.TYPE_INT_RGB);
		g2d = backbuffer.createGraphics();
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		image = tk.getImage(getURL("/resources/images/explosion.png"));
		
		gameloop = new Thread(this);
		gameloop.start();
	}
	
	private URL getURL(String fileName) {
		URL url = null;
		try {
			url = this.getClass().getResource(fileName);
		} catch (Exception e) {}
		return url;
	}
	@Override
	public void run() {
		Thread t = Thread.currentThread();
		while (t == gameloop) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameUpdate();
		}
	}
	
	public void gameUpdate() {
		g2d.setColor(Color.BLACK);
		g2d.fill(new Rectangle(0, 0, scrWidth-1, scrHeight-1));
		
		drawFrame(image, g2d, pos.x, pos.y, 6, currentFrame, 96, 96);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Position: " + pos.x + ", " + pos.y, 10, 50);
		g2d.drawString("Animation: " + currentFrame, 10, 70);
		
		frameCount++;
		if (frameCount > frameDelay) {
			frameCount = 0;
			currentFrame += animationDirection;
			if (currentFrame > totalFrames - 1) {
				currentFrame = 0;
				pos.x = rand.nextInt(scrWidth - 128);
				pos.y = rand.nextInt(scrHeight - 128);
			}
			else if (currentFrame < 0) {
				currentFrame = totalFrames - 1;
			}
		}
		repaint();
	}
	
	public void paint(Graphics g) {
		g.drawImage(backbuffer, 0, 0, this);
	}
	
	public void drawFrame(Image source, Graphics2D dest, int x, int y, int cols, int frame,
			int width, int height) {
		int fx = (frame % cols) * width;
		int fy = (frame / cols) * height;
		dest.drawImage(source, x, y, x+width, y+height, fx, fy, fx+width, fx+height, this);
	}
}
