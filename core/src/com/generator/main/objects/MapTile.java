package com.generator.main.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.generator.main.enums.TileIdents;

public class MapTile {
    int width;
    TileIdents ident;
    BaseComponent assignedComponent;
    Color colour;
    Rectangle rect;

    public MapTile(int width, TileIdents ident){
        this.width = width;
        this.ident = ident;
        assignedComponent = null;
        colour = new Color(0,0,0,1);
    }

    public TileIdents getIdent() {
        return ident;
    }

    public void setIdent(TileIdents ident) {
        this.ident = ident;
    }

    public void setAssignedComponent(BaseComponent assignedComponent) {
        this.assignedComponent = assignedComponent;
    }

    public BaseComponent getAssignedComponent() {
        return assignedComponent;
    }

    public void setColour(Color colour) {
        this.colour.set(colour);
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }
    public Rectangle getRect() {
        return rect;
    }

    public Color getColour(){
        return this.colour;
    }
}
