package com.holdtheroof.beat.runner.geometry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;;

/**
 * Created by gary on 07/09/17.
 */

public class Ground extends Rectangle{


    private Texture texture;




    public Ground(float x, float y, Texture texture) {
        super.setX(x);
        super.setY(y);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
