package com.generator.main.objects;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;

public class HeatMap {
    private int[][] heatmap;
    private ObjectMap<String, Integer> constraints;

    public HeatMap(int[][] heatmap, ObjectMap<String, Integer> constraints){
        this.heatmap = heatmap;
        this.constraints = constraints;
    }

    public int[][] getHeatmap() {
        return heatmap;
    }

    public ObjectMap<String, Integer> getConstraints() {
        return constraints;
    }

    public void setHeatmap(int[][] heatmap) {
        this.heatmap = heatmap;
    }

    public void setConstraints(ObjectMap<String, Integer> constraints) {
        this.constraints = constraints;
    }
}
