package com.game.util;

import com.game.model.AnimatedSprite;
import com.game.model.Point2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

public class AnimationClassDemo extends JFrame implements Runnable {
	static int scrWidth = 640;
	static int scrHeight = 480;
	Thread gameloop;
	Random rand = new Random();
	
	BufferedImage backbuffer;
	Graphics2D g2d;
	
	AnimatedSprite sprite;
	
	public static void main(String[] args) {
		new AnimationClassDemo();
	}
	
	public AnimationClassDemo() {
		super("Animated Class Demo");
		setSize(scrWidth, scrHeight);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		backbuffer = new BufferedImage(scrWidth, scrHeight, BufferedImage.TYPE_INT_RGB);
		g2d = backbuffer.createGraphics();
		
		/*sprite = new AnimatedSprite(this, g2d);
		sprite.load("/resources/images/explosion.png", 6, 5, 96, 96);
		sprite.setPosition(new Point2D(300,200));
		sprite.setFrameDelay(10);
		sprite.setTotalFrames(30);
		sprite.setVelocity(new Point2D(1,1));
		sprite.setRotationRate(1.0);*/
		
		gameloop = new Thread(this);
		gameloop.start();
	}
	@Override
	public void run() {
		Thread t = Thread.currentThread();
		while (t == gameloop) {
			try {
				t.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameUpdate();
		}
	}
	
	public void gameUpdate() {
		g2d.setColor(Color.BLACK);
		g2d.fill(new Rectangle(0, 0, scrWidth-1, scrHeight-1));
		
		sprite.draw();
		
		if (sprite.position().getX() < 0 || sprite.position().getX() > scrWidth-128)
			sprite.velocity().setX(sprite.velocity().getX() * (-1));
		if (sprite.position().getY() < 0 || sprite.position().getY() > scrHeight-128)
			sprite.velocity().setY(sprite.velocity().getY() * (-1));
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Position: " + sprite.position().getX() + ", " + sprite.position().getY(), 10, 40);
		g2d.drawString("Velocity: " + sprite.velocity().getX() + ", " + sprite.velocity().getY(), 10, 60);
		g2d.drawString("Animation: " + sprite.currentFrame(), 10, 80);
		
		repaint();
	}
	
	public void paint(Graphics g) {
		g.drawImage(backbuffer, 0, 0, this);
	}
}
