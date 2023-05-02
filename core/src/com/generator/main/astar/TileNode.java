package com.generator.main.astar;

public class TileNode implements GraphNode{
    private final String id;
    private final int x;
    private final int y;

    public TileNode(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


}
