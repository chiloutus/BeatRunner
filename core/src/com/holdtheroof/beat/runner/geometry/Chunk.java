package com.holdtheroof.beat.runner.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gary on 06/09/17.
 */

public class Chunk {

    private List<Ground> segments = new ArrayList<Ground>();

    public Chunk() {
    }

    public Chunk(List<Ground> segments) {
        this.segments = segments;
    }

    public void add(Ground ground) {
        segments.add(ground);
    }

    public List<Ground> getSegments() {
        return segments;
    }

    public Ground getTail() {
        return segments.get(0);
    }

    public Ground getHead() {
        return segments.get(segments.size() -1);
    }
}
