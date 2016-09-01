package com.game.util;

import java.applet.Applet;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

import javax.swing.JFrame;

public class ImageEntity extends BaseGameEntity {
	protected Image image;
	protected Applet frame;
	protected AffineTransform at;
	protected Graphics2D g2d;
	
	public ImageEntity(Applet a) {
		frame = a;
		setImage(null);
		setAlive(true);
	}
	
	public Image getImage() { return image; }
	
	public void setImage(Image image) {
		this.image = image;
		double x = frame.getSize().width/2 - width()/2;
		double y = frame.getSize().height/2 - height()/2;
		at = AffineTransform.getTranslateInstance(x, y);
	}
	
	public int width() {
		if (image != null)
			return image.getWidth(frame);
		else
			return 0;
	}
	
	public int height() {
		if (image != null)
			return image.getHeight(frame);
		else
			return 0;
	}
	
	public double getCenterX() {
		return getX() + width() / 2;
	}
	
	public double getCenterY() {
		return getY() + height() / 2;
	}
	
	public void setGraphics(Graphics2D g) {
		g2d = g;
	}
	
	private URL getURL(String fileName) {
		URL url = null;
		try {
			url = this.getClass().getResource(fileName);
		} catch (Exception e) {}
		return url;
	}
	
	public void load(String fileName) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		URL url = getURL(fileName);
		if (url != null) {
			image = tk.getImage(url);
			while(image.getWidth(frame) <= 0);
		}
		double x = frame.getSize().width/2 - width()/2;
		double y = frame.getSize().height/2 - height()/2;
		at = AffineTransform.getTranslateInstance(x, y);
	}
	
	public void transform() {
		at.setToIdentity();
		at.translate((int)getX() + width()/2, (int)getY() + height()/2);
		at.rotate(Math.toRadians(getFaceAngle()));
		at.translate(-width()/2, -height()/2);
	}
	
	public void draw() {
		g2d.drawImage(getImage(), at, frame);
	}
	
	public Rectangle getBounds() {
		Rectangle r = new Rectangle((int)getX(), (int)getY(), width(), height());
		return r;
	}

	public Graphics2D getG2d() {
		return g2d;
	}

	public Applet getFrame() {
		return frame;
	}

	public AffineTransform getAt() {
		return at;
	}
}
