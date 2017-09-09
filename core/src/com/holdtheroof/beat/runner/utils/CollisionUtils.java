package com.holdtheroof.beat.runner.utils;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gary on 09/09/17.
 */

public class CollisionUtils {

    public static boolean isOntopOf(Rectangle r1, Rectangle r2) {
        //its ontop of
        if(r1.getY() <= (r2.getY() + r2.getHeight()*2)) {
            if(r1.getX() >= r2.getX() && r1.getX() <= (r2.getX() + r2.getWidth()*2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockedBy(Rectangle r1, Rectangle r2) {
        if(r1.getY() > r2.getY() && r1.getY() < (r2.getY() + r2.getHeight()*2)) {
            if(r1.getX() + r1.getWidth()*2 >= r2.getX()) {
                return true;
            }
        }
        return false;
    }
}
