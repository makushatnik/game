package com.game.model;

import com.game.model.BaseVectorShape;

import java.awt.*;

/**
 * Created by Ageev Evgeny on 19.04.2016.
 */
public class Ship extends BaseVectorShape {
    private int[] shipx = {-6, -3, 0, 3, 6, 0};
    private int[] shipy = {6, 7, 7, 7, 6, -7};

    public Rectangle getBounds() {
        Rectangle r = new Rectangle((int)getX()-6, (int)getY()-6, 12, 12);
        return r;
    }

    Ship() {
        setShape(new Polygon(shipx, shipy, shipx.length));
        setAlive(true);
    }
}
