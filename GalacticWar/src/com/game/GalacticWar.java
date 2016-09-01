package com.game;

import com.game.engine.Game;
import com.game.model.AnimatedSprite;
import com.game.model.Point2D;
import com.game.util.ImageEntity;
import com.game.util.MidiSequence;
import com.game.util.SoundClip;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Ageev Evgeny on 24.04.2016.
 */
public class GalacticWar extends Game {
    private static final String imgPath = "/resources/images/";
    private static final String audPath = "/resources/audio/";

    private static final int FRAME_RATE = 60;
    private static final int SCR_WIDTH = 800;
    private static final int SCR_HEIGHT = 600;

    private static final int ASTEROIDS = 10;
    //private static final int BULLETS = 10;
    private static final int BULLET_SPEED = 4;
    private static final double ACCELERATION = 0.05;
    private static final double SHIP_ROTATION = 5.0;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_COLLIDED = 1;
    private static final int STATE_EXPLODING = 2;

    private static final int SPRITE_SHIP = 1;
    private static final int SPRITE_ASTEROID_BIG = 10;
    private static final int SPRITE_ASTEROID_MEDIUM = 11;
    private static final int SPRITE_ASTEROID_SMALL = 12;
    private static final int SPRITE_ASTEROID_TINY = 13;
    private static final int SPRITE_BULLET = 100;
    private static final int SPRITE_EXPLOSION = 200;
    private static final int SPRITE_POWERUP_SHIELD = 300;
    private static final int SPRITE_POWERUP_HEALTH = 301;
    private static final int SPRITE_POWERUP_250    = 302;
    private static final int SPRITE_POWERUP_500    = 303;
    private static final int SPRITE_POWERUP_1000   = 304;
    private static final int SPRITE_POWERUP_GUN    = 305;

    private static final int GAME_MENU = 0;
    private static final int GAME_RUNNING = 1;
    private static final int GAME_OVER = 2;
    private static final int GAME_WIN = 3;
    private static final int GAME_PAUSED = 4;

    private boolean showBounds = false;
    private boolean collisionTesting = true;

    ImageEntity background;
    ImageEntity bulletImage;
    ImageEntity[] bigAsteroids = new ImageEntity[5];
    ImageEntity[] medAsteroids = new ImageEntity[2];
    ImageEntity[] smlAsteroids = new ImageEntity[3];
    ImageEntity[] tnyAsteroids = new ImageEntity[4];
    ImageEntity[] explosions   = new ImageEntity[2];
    ImageEntity[] shipImage    = new ImageEntity[3];
    ImageEntity[] barImage     = new ImageEntity[2];
    ImageEntity barFrame;
    ImageEntity puShield;
    ImageEntity puHealth;
    ImageEntity pu250;
    ImageEntity pu500;
    ImageEntity pu1000;
    ImageEntity puGun;

    private int health = 20;
    private int shield = 20;
    private int score = 0;
    private int highscore = 0;
    private int firepower = 1;
    private int gameState = GAME_MENU;

    MidiSequence music = new MidiSequence();
    SoundClip shoot = new SoundClip();
    SoundClip explode = new SoundClip();
    SoundClip thrust = new SoundClip();

    Point2D zero = new Point2D(0,0);
    Point2D center = new Point2D(SCR_WIDTH / 2, SCR_HEIGHT / 2);

    Random rand = new Random();

    long collisionTimer = 0;

    boolean keyUp, keyLeft, keyRight, keyFire, keyShield, keyB, keyC;

    /*****************************************************
     * constructor
     *****************************************************/
    public GalacticWar() {
        super(FRAME_RATE, SCR_WIDTH, SCR_HEIGHT);
    }

    /*****************************************************
     * gameStartup event passed by game engine
     *****************************************************/
    @Override
    protected void gameStartup() {
        background = new ImageEntity(this);
        background.load(imgPath + "bluespace.png");

        shipImage[0] = new ImageEntity(this);
        shipImage[0].load(imgPath + "spaceship.png");
        shipImage[1] = new ImageEntity(this);
        shipImage[1].load(imgPath + "ship_thrust.png");
        shipImage[2] = new ImageEntity(this);
        shipImage[2].load(imgPath + "ship_shield.png");

        barFrame = new ImageEntity(this);
        barFrame.load(imgPath + "barframe.png");
        barImage[0] = new ImageEntity(this);
        barImage[0].load(imgPath + "bar_health.png");
        barImage[1] = new ImageEntity(this);
        barImage[1].load(imgPath + "bar_shield.png");

        puShield = new ImageEntity(this);
        puShield.load(imgPath + "powerup_shield2.png");
        puHealth = new ImageEntity(this);
        puHealth.load(imgPath + "powerup_cola.png");
        pu250 = new ImageEntity(this);
        pu250.load(imgPath + "powerup_250.png");
        pu500 = new ImageEntity(this);
        pu500.load(imgPath + "powerup_500.png");
        pu1000 = new ImageEntity(this);
        pu1000.load(imgPath + "powerup_1000.png");
        puGun = new ImageEntity(this);
        puGun.load(imgPath + "powerup_gun.png");

        AnimatedSprite ship = new AnimatedSprite(this, graphics());
        ship.setSpriteType(SPRITE_SHIP);
        ship.setImage(shipImage[0].getImage());
        ship.setFrameWidth(ship.imageWidth());
        ship.setFrameHeight(ship.imageHeight());
        ship.setPosition(center);
        ship.setAlive(true);
        ship.setState(STATE_NORMAL);
        //ship.setState(STATE_EXPLODING);
        collisionTimer = System.currentTimeMillis();
        sprites().add(ship);

        bulletImage = new ImageEntity(this);
        bulletImage.load(imgPath + "plasmashot.png");

        explosions[0] = new ImageEntity(this);
        explosions[0].load(imgPath + "explosion.png");
        explosions[1] = new ImageEntity(this);
        explosions[1].load(imgPath + "explosion2.png");

        for (int n=0; n < bigAsteroids.length; n++) {
            bigAsteroids[n] = new ImageEntity(this);
            bigAsteroids[n].load(imgPath + "asteroid" + (n+1) + ".png");
        }

        for (int n=0; n < medAsteroids.length; n++) {
            medAsteroids[n] = new ImageEntity(this);
            medAsteroids[n].load(imgPath + "medium" + (n+1) + ".png");
        }

        for (int n=0; n < smlAsteroids.length; n++) {
            smlAsteroids[n] = new ImageEntity(this);
            smlAsteroids[n].load(imgPath + "small" + (n+1) + ".png");
        }

        for (int n=0; n < tnyAsteroids.length; n++) {
            tnyAsteroids[n] = new ImageEntity(this);
            tnyAsteroids[n].load(imgPath + "asteroid" + (n+1) + ".png");
        }

        music.load(audPath + "music.mid");
        shoot.load(audPath + "shoot.au");
        explode.load(audPath + "explode.au");
        thrust.load(audPath + "thrust.au");

        pauseGame();
    }

    private void resetGame() {
        music.setLooping(true);
        music.play();

        AnimatedSprite ship = (AnimatedSprite) sprites().get(0);
        sprites().clear();

        ship.setPosition(center);
        ship.setAlive(true);
        ship.setState(STATE_NORMAL);
        collisionTimer = System.currentTimeMillis();
        ship.setVelocity(zero);
        sprites().add(ship);

        for (int n=0; n < ASTEROIDS; n++) {
            createAsteroid();
        }

        health = 20;
        shield = 20;
        score  = 0;
        firepower = 1;
    }

    /*****************************************************
     * gameTimedUpdate event passed by game engine
     *****************************************************/
    @Override
    protected void gameTimedUpdate() {
        checkInput();
        //WIN
        if (!gamePaused() && sprites().size() == 1) {
            resetGame();
            gameState = GAME_OVER;
        }
    }

    /*****************************************************
     * gameRefreshScreen event passed by game engine
     *****************************************************/
    @Override
    protected void gameRefreshScreen() {
        Graphics2D g2d = graphics();

        //AnimatedSprite ship = (AnimatedSprite) sprites().get(0);

        g2d.drawImage(background.getImage(), 0, 0, SCR_WIDTH-1, SCR_HEIGHT-1, this);

        /*g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + frameRate(), 5, 10);
        long x = Math.round(ship.position().getX());
        long y = Math.round(ship.position().getY());
        g2d.drawString("Ship: " + x + "," + y, 5, 25);
        g2d.drawString("Move angle: " + Math.round(ship.getMoveAngle()) + 90, 5, 40);
        g2d.drawString("Face angle: " + Math.round(ship.getFaceAngle()), 5, 55);

        if (ship.state() == STATE_NORMAL)
            g2d.drawString("State: NORMAL", 5, 70);
        else if (ship.state() == STATE_COLLIDED)
            g2d.drawString("State: COLLIDED", 5, 70);
        else if (ship.state() == STATE_EXPLODING)
            g2d.drawString("State: EXPLODING", 5, 70);

        g2d.drawString("Sprites: " + sprites().size(), 5, 120);

        if (showBounds) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("BOUNDING BOXES", SCR_WIDTH - 150, 10);
        }

        if (collisionTesting) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("COLLISION TESTING", SCR_WIDTH - 150, 25);
        }*/

        if (gameState == GAME_MENU) {
            g2d.setFont(new Font("Verdana", Font.BOLD, 36));
            g2d.setColor(Color.BLACK);
            g2d.drawString("GALACTIC WAR", 252, 202);
            g2d.setColor(new Color(200,30,30));
            g2d.drawString("GALACTIC WAR", 250, 200);

            int x = 270, y = 15;
            g2d.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
            g2d.setColor(Color.YELLOW);
            g2d.drawString("CONTROLS:", x, ++y*20);
            g2d.drawString("ROTATE - Left/Right Arrows", x+20, ++y*20);
            g2d.drawString("THRUST - Up Arrow", x+20, ++y*20);
            g2d.drawString("SHIELD - Shift key (no scoring)", x+20, ++y*20);
            g2d.drawString("FIRE - Ctrl key", x+20, ++y*20);

            g2d.setColor(Color.WHITE);
            g2d.drawString("POWERUPS INCREASE FIREPOWER!", 240, 480);

            g2d.setFont(new Font("Ariel", Font.BOLD, 24));
            g2d.setColor(Color.ORANGE);
            g2d.drawString("Press ENTER to start", 280, 570);
        }
        else if (gameState == GAME_RUNNING) {
            AnimatedSprite ship = (AnimatedSprite) sprites().get(0);

            g2d.setColor(Color.WHITE);
            if (ship.state() == STATE_NORMAL)
                g2d.drawString("State: NORMAL", 5, 70);
            else if (ship.state() == STATE_COLLIDED)
                g2d.drawString("State: COLLIDED", 5, 70);
            else if (ship.state() == STATE_EXPLODING)
                g2d.drawString("State: EXPLODING", 5, 70);

            g2d.drawImage(barFrame.getImage(), SCR_WIDTH - 132, 18, this);
            for (int n=0; n < health; n++) {
                int dx = SCR_WIDTH - 130 + n*5;
                g2d.drawImage(barImage[0].getImage(), dx, 20, this);
            }
            g2d.drawImage(barFrame.getImage(), SCR_WIDTH - 132, 33, this);
            for (int n=0; n < health; n++) {
                int dx = SCR_WIDTH - 130 + n*5;
                g2d.drawImage(barImage[1].getImage(), dx, 35, this);
            }

            for (int n=0; n < firepower; n++) {
                int dx = SCR_WIDTH - 220 + n*13;
                g2d.drawImage(puGun.getImage(), dx, 17, this);
            }

            g2d.setFont(new Font("Verdana", Font.BOLD, 24));
            g2d.setColor(Color.WHITE);
            g2d.drawString("" + score, 20, 40);
            g2d.setColor(Color.RED);
            g2d.drawString("" + highscore, 350, 40);
        }
        else if (gameState == GAME_OVER) {
            g2d.setFont(new Font("Verdana", Font.BOLD, 36));
            g2d.setColor(new Color(200,30,30));
            g2d.drawString("GAME OVER", 270, 200);

            g2d.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
            g2d.setColor(Color.ORANGE);
            g2d.drawString("Press ENTER to restart", 260, 500);
        }
    }

    /*****************************************************
     * gameShutdown event passed by game engine
     *****************************************************/
    @Override
    protected void gameShutdown() {
        music.stop();
        shoot.stop();
        thrust.stop();
        explode.stop();
    }

    /*****************************************************
     * spriteUpdate event passed by game engine
     *****************************************************/
    @Override
    public void spriteUpdate(AnimatedSprite sprite) {
        switch (sprite.spriteType()) {
            case SPRITE_SHIP:
                warp(sprite);
                break;

            case SPRITE_BULLET:
                warp(sprite);
                break;

            case SPRITE_EXPLOSION:
                if (sprite.currentFrame() == sprite.totalFrames()-1) {
                    sprite.setAlive(false);
                }
                break;

            case SPRITE_ASTEROID_BIG:
            case SPRITE_ASTEROID_MEDIUM:
            case SPRITE_ASTEROID_SMALL:
            case SPRITE_ASTEROID_TINY:
                warp(sprite);
                break;

            case SPRITE_POWERUP_SHIELD:
            case SPRITE_POWERUP_HEALTH:
            case SPRITE_POWERUP_250:
            case SPRITE_POWERUP_500:
            case SPRITE_POWERUP_1000:
            case SPRITE_POWERUP_GUN:
                warp(sprite);

                double rot = sprite.rotationRate();
                if (sprite.getFaceAngle() > 350) {
                    sprite.setRotationRate(rot *-1);
                    sprite.setFaceAngle(350);
                }
                else if (sprite.getFaceAngle() < 10) {
                    sprite.setRotationRate(rot *-1);
                    sprite.setFaceAngle(10);
                }
                Arrays.asList(new int[]{2});
                break;
        }
    }

    public final void top() {}

    /*****************************************************
     * spriteDraw event passed by game engine
     * called by the game class after each sprite is drawn
     * to give you a chance to manipulate the sprite
     *****************************************************/
    @Override
    public void spriteDraw(AnimatedSprite sprite) {
        if (showBounds) {
            if (sprite.collided())
                sprite.drawBounds(Color.RED);
            else
                sprite.drawBounds(Color.BLUE);
        }
    }

    /*****************************************************
     * spriteDying event passed by game engine
     * called after a sprite's age reaches its lifespan
     * at which point it will be killed off, and then removed from
     * the linked list. you can cancel the purging process here.
     *****************************************************/
    @Override
    public void spriteDying(AnimatedSprite sprite) {

    }

    /*****************************************************
     * spriteCollision event passed by game engine
     *****************************************************/
    @Override
    public void spriteCollision(AnimatedSprite spr1, AnimatedSprite spr2) {
        if (!collisionTesting) return;

        switch (spr1.spriteType()) {
            case SPRITE_BULLET:
                if (isAsteroid(spr2.spriteType())) {
                    bumpScore(5);

                    spr1.setAlive(false);
                    spr2.setAlive(false);
                    breakAsteroid(spr2);

                    explode.play();
                }
                break;
            case SPRITE_SHIP:
                if (isAsteroid(spr2.spriteType())) {
                    if (spr1.state() == STATE_NORMAL) {
                        if (keyShield) {
                            shield -= 1;
                        } else {
                            explode.play();

                            collisionTimer = System.currentTimeMillis();
                            spr1.setVelocity(zero);
                            double x = spr1.position().getX() - 10;
                            double y = spr2.position().getY() - 10;
                            startBigExplosion(new Point2D(x, y));
                            spr1.setState(STATE_EXPLODING);

                            health -= 1;
                            if (health < 0) gameState = GAME_OVER;

                            firepower--;
                            if (firepower < 1) firepower = 1;
                        }
                        spr2.setAlive(false);
                        breakAsteroid(spr2);
                    }
                    else if (spr1.state() == STATE_EXPLODING) {
                        if (collisionTimer + 3000 < System.currentTimeMillis()) {
                            spr1.setState(STATE_NORMAL);
                        }
                    }
                }
                break;
            case SPRITE_POWERUP_SHIELD:
                if (spr2.spriteType() == SPRITE_SHIP) {
                    shield += 5;
                    if (shield > 20) shield = 20;
                    spr1.setAlive(false);
                }
                break;
            case SPRITE_POWERUP_HEALTH:
                if (spr2.spriteType() == SPRITE_SHIP) {
                    health += 5;
                    if (shield > 20) shield = 20;
                    spr1.setAlive(false);
                }
                break;
            case SPRITE_POWERUP_250:
                if (spr2.spriteType() == SPRITE_SHIP) {
                    bumpScore(250);
                    spr1.setAlive(false);
                }
                break;
            case SPRITE_POWERUP_500:
                if (spr2.spriteType() == SPRITE_SHIP) {
                    bumpScore(500);
                    spr1.setAlive(false);
                }
                break;
            case SPRITE_POWERUP_1000:
                if (spr2.spriteType() == SPRITE_SHIP) {
                    bumpScore(1000);
                    spr1.setAlive(false);
                }
                break;
            case SPRITE_POWERUP_GUN:
                if (spr2.spriteType() == SPRITE_SHIP) {
                    firepower++;
                    if (firepower > 5) firepower = 5;
                    spr1.setAlive(false);
                }
                break;
        }
    }

    /*****************************************************
     * gameKeyDown event passed by game engine
     *****************************************************/
    @Override
    protected void gameKeyDown(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                keyLeft = true;
                break;
            case KeyEvent.VK_RIGHT:
                keyRight = true;
                break;
            case KeyEvent.VK_UP:
                keyUp = true;
                break;
            case KeyEvent.VK_CONTROL:
                keyFire = true;
                break;
            case KeyEvent.VK_B:
                showBounds = !showBounds;
                break;
            case KeyEvent.VK_C:
                collisionTesting = !collisionTesting;
                break;
            case KeyEvent.VK_SPACE:
                //stop the ship
                sprites().get(0).setVelocity(zero);
                break;
            case KeyEvent.VK_ENTER:
                if (gameState == GAME_MENU) {
                    resetGame();
                    resumeGame();
                    gameState = GAME_RUNNING;
                }
                else if (gameState == GAME_OVER) {
                    resetGame();
                    resumeGame();
                    gameState = GAME_RUNNING;
                }
                break;
            case KeyEvent.VK_SHIFT:
                if (!keyUp && shield > 0)
                    keyShield = true;
                else
                    keyShield = false;
                break;
            case KeyEvent.VK_ESCAPE:
                if (gameState == GAME_RUNNING) {
                    pauseGame();
                    gameState = GAME_OVER;
                }
                break;
        }
    }

    /*****************************************************
     * gameKeyUp event passed by game engine
     *****************************************************/
    @Override
    protected void gameKeyUp(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                keyLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
                keyRight = false;
                break;
            case KeyEvent.VK_UP:
                keyUp = false;
                break;
            case KeyEvent.VK_CONTROL:
                keyFire = false;
                fireBullet();
                break;
            case KeyEvent.VK_SHIFT:
                keyShield = false;
                break;
        }
    }

    /*****************************************************
     * mouse events passed by game engine
     * the game is not currently using mouse input
     *****************************************************/
    @Override
    protected void gameMouseDown() {}
    @Override
    protected void gameMouseUp() {}
    @Override
    protected void gameMouseMove() {}

    /*****************************************************
     * break up an asteroid into smaller pieces
     *****************************************************/
    private void breakAsteroid(AnimatedSprite sprite) {
        switch (sprite.spriteType()) {
            case SPRITE_ASTEROID_BIG:
                spawnAsteroid(sprite);
                spawnAsteroid(sprite);
                spawnAsteroid(sprite);

                startBigExplosion(sprite.position());
                break;
            case SPRITE_ASTEROID_MEDIUM:
                spawnAsteroid(sprite);
                spawnAsteroid(sprite);
                spawnAsteroid(sprite);

                startSmallExplosion(sprite.position());
                break;
            case SPRITE_ASTEROID_SMALL:
                spawnAsteroid(sprite);
                spawnAsteroid(sprite);
                spawnAsteroid(sprite);

                startSmallExplosion(sprite.position());
                break;
            case SPRITE_ASTEROID_TINY:
                spawnPowerup(sprite);

                startSmallExplosion(sprite.position());
                break;
        }
    }

    /*****************************************************
     * spawn a smaller asteroid based on passed sprite
     *****************************************************/
    private void spawnAsteroid(AnimatedSprite sprite) {
        AnimatedSprite ast = new AnimatedSprite(this, graphics());
        ast.setAlive(true);
        //set pseudo-random position around source sprite
        int w = sprite.getBounds().width;
        int h = sprite.getBounds().height;
        double x = sprite.position().getX() + w/2 + rand.nextInt(20) - 40;
        double y = sprite.position().getY() + h/2 + rand.nextInt(20) - 40;
        ast.setPosition(new Point2D(x,y));

        ast.setFaceAngle(rand.nextInt(360));
        ast.setMoveAngle(rand.nextInt(360));
        ast.setRotationRate(rand.nextDouble());

        double ang = ast.getMoveAngle() - 90;
        double velX = calcAngleMoveX(ang);
        double velY = calcAngleMoveY(ang);
        ast.setVelocity(new Point2D(velX,velY));

        int i = -1;
        switch (sprite.spriteType()) {
            case SPRITE_ASTEROID_BIG:
                ast.setSpriteType(SPRITE_ASTEROID_MEDIUM);

                i = rand.nextInt(medAsteroids.length);
                ast.setImage(medAsteroids[i].getImage());
                ast.setFrameWidth(medAsteroids[i].width());
                ast.setFrameHeight(medAsteroids[i].height());
                break;
            case SPRITE_ASTEROID_MEDIUM:
                ast.setSpriteType(SPRITE_ASTEROID_SMALL);

                i = rand.nextInt(smlAsteroids.length);
                ast.setImage(smlAsteroids[i].getImage());
                ast.setFrameWidth(smlAsteroids[i].width());
                ast.setFrameHeight(smlAsteroids[i].height());
                break;
            case SPRITE_ASTEROID_SMALL:
                ast.setSpriteType(SPRITE_ASTEROID_TINY);

                i = rand.nextInt(tnyAsteroids.length);
                ast.setImage(tnyAsteroids[i].getImage());
                ast.setFrameWidth(tnyAsteroids[i].width());
                ast.setFrameHeight(tnyAsteroids[i].height());
                break;
        }

        sprites().add(ast);
    }

    /*****************************************************
     * create a random powerup at the supplied sprite location
     *****************************************************/
    private void spawnPowerup(AnimatedSprite sprite) {
        int n = rand.nextInt(100);
        if (n > 12) return;

        AnimatedSprite spr = new AnimatedSprite(this, graphics());
        spr.setRotationRate(8);
        spr.setPosition(sprite.position());
        double velX = rand.nextDouble();
        double velY = rand.nextDouble();
        spr.setVelocity(new Point2D(velX,velY));
        spr.setLifespan(1500);
        spr.setAlive(true);

        switch (rand.nextInt(6)) {
            case 0:
                spr.setImage(puShield.getImage());
                spr.setSpriteType(SPRITE_POWERUP_SHIELD);
                sprites().add(spr);
                break;
            case 1:
                spr.setImage(puHealth.getImage());
                spr.setSpriteType(SPRITE_POWERUP_HEALTH);
                sprites().add(spr);
                break;
            case 2:
                spr.setImage(pu250.getImage());
                spr.setSpriteType(SPRITE_POWERUP_250);
                sprites().add(spr);
                break;
            case 3:
                spr.setImage(pu500.getImage());
                spr.setSpriteType(SPRITE_POWERUP_500);
                sprites().add(spr);
                break;
            case 4:
                spr.setImage(pu1000.getImage());
                spr.setSpriteType(SPRITE_POWERUP_1000);
                sprites().add(spr);
                break;
            case 5:
                spr.setImage(puGun.getImage());
                spr.setSpriteType(SPRITE_POWERUP_GUN);
                sprites().add(spr);
                break;
        }
    }

    /*****************************************************
     * create a random "big" asteroid
     *****************************************************/
    private void createAsteroid() {
        AnimatedSprite ast = new AnimatedSprite(this, graphics());
        ast.setAlive(true);
        ast.setSpriteType(SPRITE_ASTEROID_BIG);

        int i = rand.nextInt(bigAsteroids.length);
        ast.setImage(bigAsteroids[i].getImage());
        ast.setFrameWidth(bigAsteroids[i].width());
        ast.setFrameHeight(bigAsteroids[i].height());

        int x = rand.nextInt(SCR_WIDTH - 128);
        int y = rand.nextInt(SCR_HEIGHT - 128);
        ast.setPosition(new Point2D(x,y));

        ast.setFaceAngle(rand.nextInt(360));
        ast.setMoveAngle(rand.nextInt(360));
        ast.setRotationRate(rand.nextDouble());

        double ang = ast.getMoveAngle() - 90;
        double velX = calcAngleMoveX(ang);
        double velY = calcAngleMoveY(ang);
        ast.setVelocity(new Point2D(velX,velY));

        sprites().add(ast);
    }

    /*****************************************************
     * returns true if passed sprite type is an asteroid type
     *****************************************************/
    private boolean isAsteroid(int spriteType) {
        switch (spriteType) {
            case SPRITE_ASTEROID_BIG:
            case SPRITE_ASTEROID_MEDIUM:
            case SPRITE_ASTEROID_SMALL:
            case SPRITE_ASTEROID_TINY:
                return true;
            default:
                return false;
        }
    }

    /*****************************************************
     * process keys that have been pressed
     *****************************************************/
    public void checkInput() {
        if (gameState != GAME_RUNNING) return;

        AnimatedSprite ship = (AnimatedSprite) sprites().get(0);
        if (keyLeft) {
            double newF = ship.getFaceAngle() - SHIP_ROTATION;
            if (newF < 0) newF = 360-SHIP_ROTATION;
            ship.setFaceAngle(newF);
        }
        else if (keyRight) {
            double newF = ship.getFaceAngle() + SHIP_ROTATION;
            if (newF > 360) newF = SHIP_ROTATION;
            ship.setFaceAngle(newF);
        }
        if (keyUp) {
            ship.setImage(shipImage[1].getImage());
            applyThrust();
        }
        else if (keyShield) {
            ship.setImage(shipImage[2].getImage());
        }
        else
            ship.setImage(shipImage[0].getImage());
    }

    /*****************************************************
     * increase the thrust of the ship based on facing angle
     *****************************************************/
    public void applyThrust() {
        AnimatedSprite ship = (AnimatedSprite) sprites().get(0);
        ship.setMoveAngle(ship.getFaceAngle() - 90);

        double velX = ship.velocity().getX();
        velX += calcAngleMoveX(ship.getMoveAngle()) * ACCELERATION;
        double velY = ship.velocity().getY();
        velY += calcAngleMoveY(ship.getMoveAngle()) * ACCELERATION;

        ship.setVelocity(new Point2D(velX,velY));

        thrust.play();
    }

    /*****************************************************
     * fire a bullet from the ship's position and orientation
     *****************************************************/
    private void fireBullet() {
        /*AnimatedSprite ship = (AnimatedSprite) sprites().get(0);

        AnimatedSprite bullet = new AnimatedSprite(this, graphics());
        bullet.setImage(bulletImage.getImage());
        bullet.setFrameWidth(bulletImage.width());
        bullet.setFrameHeight(bulletImage.height());
        bullet.setSpriteType(SPRITE_BULLET);
        bullet.setAlive(true);
        bullet.setLifespan(200);
        bullet.setFaceAngle(ship.getFaceAngle());
        bullet.setMoveAngle(ship.getFaceAngle() - 90);

        double x = ship.center().getX() - bullet.imageWidth() / 2;
        double y = ship.center().getY() - bullet.imageHeight() / 2;
        bullet.setPosition(new Point2D(x,y));

        double angle = bullet.getMoveAngle();
        double svx = calcAngleMoveX(angle) * BULLET_SPEED;
        double svy = calcAngleMoveY(angle) * BULLET_SPEED;
        bullet.setVelocity(new Point2D(svx, svy));

        sprites().add(bullet);*/

        AnimatedSprite[] bullets = new AnimatedSprite[6];
        switch (firepower) {
            case 1:
                bullets[0] = stockBullet();
                sprites().add(bullets[0]);
                break;
            case 2:
                bullets[0] = stockBullet();
                adjustDirection(bullets[0], -4);
                sprites().add(bullets[0]);
                bullets[1] = stockBullet();
                adjustDirection(bullets[1], 4);
                sprites().add(bullets[1]);
                break;
            case 3:
                bullets[0] = stockBullet();
                adjustDirection(bullets[0], -4);
                sprites().add(bullets[0]);
                bullets[1] = stockBullet();
                sprites().add(bullets[1]);
                bullets[2] = stockBullet();
                adjustDirection(bullets[2], 4);
                sprites().add(bullets[2]);
                break;
            case 4:
                bullets[0] = stockBullet();
                adjustDirection(bullets[0], -5);
                sprites().add(bullets[0]);
                bullets[1] = stockBullet();
                adjustDirection(bullets[1], 5);
                sprites().add(bullets[1]);
                bullets[2] = stockBullet();
                adjustDirection(bullets[2], -10);
                sprites().add(bullets[2]);
                bullets[3] = stockBullet();
                adjustDirection(bullets[3], 10);
                sprites().add(bullets[3]);
                break;
            case 5:
                bullets[0] = stockBullet();
                adjustDirection(bullets[0], -6);
                sprites().add(bullets[0]);
                bullets[1] = stockBullet();
                adjustDirection(bullets[1], 6);
                sprites().add(bullets[1]);
                bullets[2] = stockBullet();
                adjustDirection(bullets[2], -15);
                sprites().add(bullets[2]);
                bullets[3] = stockBullet();
                adjustDirection(bullets[3], 15);
                sprites().add(bullets[3]);
                bullets[2] = stockBullet();
                adjustDirection(bullets[2], -60);
                sprites().add(bullets[2]);
                bullets[3] = stockBullet();
                adjustDirection(bullets[3], 60);
                sprites().add(bullets[3]);
                break;
        }

        shoot.play();
    }

    private void adjustDirection(AnimatedSprite spr, double angle) {
        angle = spr.getFaceAngle() + angle;
        if (angle < 0) angle += 360;
        else if (angle > 360) angle -= 360;
        spr.setFaceAngle(angle);
        spr.setMoveAngle(spr.getFaceAngle() + 90);
        angle = spr.getMoveAngle();
        double svX = calcAngleMoveX(angle) * BULLET_SPEED;
        double svY = calcAngleMoveY(angle) * BULLET_SPEED;
        spr.setVelocity(new Point2D(svX,svY));
    }

    private AnimatedSprite stockBullet() {
        AnimatedSprite ship = (AnimatedSprite) sprites().get(0);

        AnimatedSprite bul = new AnimatedSprite(this, graphics());
        bul.setImage(bulletImage.getImage());
        bul.setFrameWidth(bulletImage.width());
        bul.setFrameHeight(bulletImage.height());
        bul.setSpriteType(SPRITE_BULLET);
        bul.setAlive(true);
        bul.setLifespan(90);
        bul.setFaceAngle(ship.getFaceAngle());
        bul.setMoveAngle(ship.getFaceAngle() - 90);

        double x = ship.center().getX() - bul.imageWidth() / 2;
        double y = ship.center().getY() - bul.imageHeight() / 2;
        bul.setPosition(new Point2D(x,y));

        double angle = bul.getMoveAngle();
        double svx = calcAngleMoveX(angle) * BULLET_SPEED;
        double svy = calcAngleMoveY(angle) * BULLET_SPEED;
        bul.setVelocity(new Point2D(svx, svy));

        return bul;
    }

    //collisions with ship don't count
    private void bumpScore(int howmuch) {
        score += howmuch;
        if (score > highscore) highscore = score;
    }
    /*****************************************************
     * launch a big explosion at the passed location
     *****************************************************/
    private void startBigExplosion(Point2D point) {
        AnimatedSprite expl = new AnimatedSprite(this, graphics());
        expl.setSpriteType(SPRITE_EXPLOSION);
        expl.setAlive(true);
        expl.setAnimImage(explosions[0].getImage());
        expl.setTotalFrames(16);
        expl.setColumns(4);
        expl.setFrameWidth(96);
        expl.setFrameHeight(96);
        expl.setFrameDelay(2);
        expl.setPosition(point);

        sprites().add(expl);
    }

    /*****************************************************
     * launch a small explosion at the passed location
     *****************************************************/
    public void startSmallExplosion(Point2D point) {
        AnimatedSprite expl = new AnimatedSprite(this, graphics());
        expl.setSpriteType(SPRITE_EXPLOSION);
        expl.setAlive(true);
        expl.setAnimImage(explosions[1].getImage());
        expl.setTotalFrames(8);
        expl.setColumns(4);
        expl.setFrameWidth(40);
        expl.setFrameHeight(40);
        expl.setFrameDelay(2);
        expl.setPosition(point);

        sprites().add(expl);
    }

    /*****************************************************
     * cause sprite to warp around the edges of the screen
     *****************************************************/
    private void warp(AnimatedSprite sprite) {
        int w = sprite.frameWidth() - 1;
        int h = sprite.frameHeight() - 1;

        if (sprite.position().getX() < 0-w)
            sprite.position().setX(SCR_WIDTH);
        else if (sprite.position().getX() > SCR_WIDTH)
            sprite.position().setX(0-w);
        if (sprite.position().getY() < 0-h)
            sprite.position().setY(SCR_HEIGHT);
        else if (sprite.position().getY() > SCR_HEIGHT)
            sprite.position().setY(0-h);
    }
}
