package com.generator.main.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.generator.main.objects.MapTile;
import com.generator.main.enums.TileIdents;

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
                Rectangle tempRect = new Rectangle(j*tileWidth, i*tileWidth, tileWidth, tileWidth);
                if (input.contains(j,i)){
                    output[i][j] = createFilledMapTile(TileIdents.HULL);
                    output[i][j].setColour(new Color(255,0,0,1));
                } else {
                    output[i][j] = createBlankMapTile();
                }
                output[i][j].setRect(tempRect);
            }
        }
        return output;
    }

    private MapTile createBlankMapTile(){
        return new MapTile(this.tileWidth, TileIdents.EMPTY);
    }

    private MapTile createFilledMapTile(TileIdents ident){
        return new MapTile(this.tileWidth, ident);
    }
}
