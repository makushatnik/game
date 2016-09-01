package com.game.engine;

import com.game.model.AnimatedSprite;
import com.game.model.Point2D;
import com.game.model.Sprite;
import com.game.util.ImageEntity;
import com.game.util.SoundClip;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Ageev Evgeny on 24.04.2016.
 */
public abstract class Game extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    Thread gameloop;

    private LinkedList<Sprite> _sprites;
    public LinkedList<Sprite> sprites() { return _sprites; }

    private BufferedImage backBuffer;
    private Graphics2D g2d;
    private int screenWidth, screenHeight;

    private Point2D mousePos = new Point2D(0,0);
    private boolean[] mouseButtons = new boolean[4];

    private int _frameCount = 0, _frameRate = 0;
    private int desiredRate;
    long startTime = System.currentTimeMillis();

    public Applet applet() { return this; }

    private boolean _gamePaused = false;
    public boolean gamePaused() { return _gamePaused; }
    public void pauseGame() { _gamePaused = true; }
    public void resumeGame() { _gamePaused = false; }

    abstract protected void gameStartup();
    abstract protected void gameTimedUpdate();
    abstract protected void gameRefreshScreen();
    abstract protected void gameShutdown();
    abstract protected void gameKeyDown(int keyCode);
    abstract protected void gameKeyUp(int keyCode);
    abstract protected void gameMouseDown();
    abstract protected void gameMouseUp();
    abstract protected void gameMouseMove();
    abstract protected void spriteUpdate(AnimatedSprite sprite);
    abstract protected void spriteDraw(AnimatedSprite sprite);
    abstract protected void spriteDying(AnimatedSprite sprite);
    abstract protected void spriteCollision(AnimatedSprite spr1, AnimatedSprite spr2);

    public Game(int frameRate, int width, int height) {
        desiredRate = frameRate;
        screenWidth = width;
        screenHeight = height;
    }

    public Graphics2D graphics() { return g2d; }

    public int frameRate() { return _frameRate; }

    public boolean mouseButton(int btn) { return mouseButtons[btn]; }
    public Point2D mousePos() { return mousePos; }

    public void init() {
        setName("Galactic War");
        setSize(screenWidth, screenHeight);
        backBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        g2d = backBuffer.createGraphics();

        _sprites = new LinkedList<>();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        gameStartup();
    }

    public void update(Graphics g) {
        _frameCount++;
        if (System.currentTimeMillis() > startTime + 1000) {
            startTime = System.currentTimeMillis();
            _frameRate = _frameCount;
            _frameCount = 0;

            purgeSprites();
        }

        gameRefreshScreen();

        if (!gamePaused()) {
            drawSprites();
        }

        paint(g);
    }

    public void paint(Graphics g) {
        g.drawImage(backBuffer, 0, 0, this);
    }

    public void start() {
        gameloop = new Thread(this);
        gameloop.start();
    }

    public void stop() {
        gameloop = null;
        gameShutdown();
    }
    @Override
    public void run() {
        Thread t = Thread.currentThread();
        while (t == gameloop) {
            try {
                Thread.sleep(1000 / desiredRate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!gamePaused()) {
                updateSprites();
                testCollisions();
            }
            gameTimedUpdate();
            repaint();
        }
    }

    protected void updateSprites() {
        for (int i=0; i < _sprites.size(); i++) {
            AnimatedSprite spr = (AnimatedSprite) _sprites.get(i);
            if (spr.isAlive()) {
                spr.updatePosition();
                spr.updateRotation();
                spr.updateAnimation();
                spriteUpdate(spr);
                spr.updateLifetime();
                if (!spr.isAlive()) {
                    spriteDying(spr);
                }
            }
        }
    }

    protected void testCollisions() {
        for (int i=0; i < _sprites.size(); i++) {
            AnimatedSprite spr1 = (AnimatedSprite) _sprites.get(i);
            if (spr1.isAlive()) {
                for (int j=0; j < _sprites.size(); j++) {
                    if (i != j) {
                        AnimatedSprite spr2 = (AnimatedSprite) _sprites.get(j);
                        if (spr2.isAlive()) {
                            if (spr2.collidesWith(spr1)) {
                                spriteCollision(spr1, spr2);
                                break;
                            } else
                                spr1.setCollided(false);
                        }
                    }
                }
            }
        }
    }

    protected void drawSprites() {
        for (int n=0; n < _sprites.size(); n++) {
            AnimatedSprite spr = (AnimatedSprite) _sprites.get(n);
            if (spr.isAlive()) {
                spr.updateFrame();
                spr.transform();
                spr.draw();
                spriteDraw(spr);
            }
        }
    }

    private void purgeSprites() {
        for (int n=0; n < _sprites.size(); n++) {
            AnimatedSprite spr = (AnimatedSprite) _sprites.get(n);
            if (!spr.isAlive()) {
                _sprites.remove(n);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gameKeyUp(e.getKeyCode());
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        gameKeyDown(e.getKeyCode());
    }

    private void checkButtons(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                mouseButtons[1] = true;
                mouseButtons[2] = false;
                mouseButtons[3] = false;
                break;
            case MouseEvent.BUTTON2:
                mouseButtons[1] = false;
                mouseButtons[2] = true;
                mouseButtons[3] = false;
                break;
            case MouseEvent.BUTTON3:
                mouseButtons[1] = false;
                mouseButtons[2] = false;
                mouseButtons[3] = true;
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        checkButtons(e);
        mousePos.setX(e.getX());
        mousePos.setY(e.getY());
        gameMouseDown();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        checkButtons(e);
        mousePos.setX(e.getX());
        mousePos.setY(e.getY());
        gameMouseUp();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mousePos.setX(e.getX());
        mousePos.setY(e.getY());
        gameMouseMove();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePos.setX(e.getX());
        mousePos.setY(e.getY());
        gameMouseMove();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        checkButtons(e);
        mousePos.setX(e.getX());
        mousePos.setY(e.getY());
        gameMouseDown();
        gameMouseMove();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        checkButtons(e);
        mousePos.setX(e.getX());
        mousePos.setY(e.getY());
        gameMouseMove();
    }

    public double calcAngleMoveX(double angle) {
        return (double)(Math.cos(angle * Math.PI/180));
    }

    public double calcAngleMoveY(double angle) {
        return (double)(Math.sin(angle * Math.PI/180));
    }
}
