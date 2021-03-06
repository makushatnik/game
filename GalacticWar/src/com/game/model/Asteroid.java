package com.game.model;

import java.awt.*;

/**
 * Created by Ageev Evgeny on 19.04.2016.
 */
public class Asteroid extends VectorEntity {
    private int[] astx = {-20, -13, 0, 20, 22, 20, 12, 2, -10, -22, -16};
    private int[] asty = {20, 23, 17, 20, 16, -20, -22, -14, -17, -20, -5};

    protected double rotVel;
    public double getRotationVelocity() { return rotVel; }
    public void setRotationVelocity(double v) { rotVel = v; }

    public Rectangle getBounds() {
        Rectangle r = new Rectangle((int)getX()-20, (int)getY()-20, 40, 40);
        return r;
    }

    public Asteroid() {
        setShape(new Polygon(astx, asty, astx.length));
        setAlive(true);
        setRotationVelocity(0.0);
    }
}