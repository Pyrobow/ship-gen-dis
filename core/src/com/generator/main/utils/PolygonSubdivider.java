package com.generator.main.utils;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.generator.main.objects.MapTile;

public class PolygonSubdivider {
    int tileWidth;

    public PolygonSubdivider(int tileWidth){
        this.tileWidth = tileWidth;
    }

    public MapTile[][] polygonToArray(Polygon input){
        Rectangle bound = input.getBoundingRectangle();
        //Guarantees that the input polygon has its leftmost point lying on the line y=0
        if (bound.getX() != 0){
            input.translate(-bound.getX(), 0);
            bound.set(input.getBoundingRectangle());
        }
        MapTile[][] output = new MapTile[(int) bound.getHeight()][(int) bound.getWidth()];
        for (int i = 0; i < output.length; i++){
            for (int j = 0; j < output[i].length; j++){
                if (input.contains(j,i)){
                    output[i][j] = createFilledMapTile("poly");
                } else {
                    output[i][j] = createBlankMapTile();
                }
            }
        }
        return output;
    }

    private MapTile createBlankMapTile(){
        return new MapTile(this.tileWidth, "empty");
    }

    private MapTile createFilledMapTile(String ident){
        return new MapTile(this.tileWidth, ident);
    }
}
