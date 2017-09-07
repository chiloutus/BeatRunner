package com.holdtheroof.beat.runner.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gary on 06/09/17.
 */

public class Player extends Rectangle {

    private Texture texture;
    private PlayerState state;


    public Player(float x, float y, Texture texture) {
        super.setX(x);
        super.setY(y);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }
}
