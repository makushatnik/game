package com.game.model;

import com.game.model.BaseVectorShape;

import java.awt.*;

/**
 * Created by Ageev Evgeny on 23.04.2016.
 */
public class VectorEntity extends BaseVectorShape {
    private Shape shape;

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    VectorEntity() {
        setShape(null);
    }
}
