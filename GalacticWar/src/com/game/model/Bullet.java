package com.game.model;

import java.awt.*;

/**
 * Created by Ageev Evgeny on 19.04.2016.
 */
public class Bullet extends VectorEntity {
    public Rectangle getBounds() {
        Rectangle r = new Rectangle((int)getX(), (int)getY(), 1, 1);
        return r;
    }

    public Bullet() {
        setShape(new Rectangle(0, 0, 1, 1));
        setAlive(false);
    }
}