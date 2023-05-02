package com.generator.main.astar;

public class ManhattanScorer implements Scorer<TileNode> {
    @Override
    public double computeCost(TileNode from, TileNode to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }
}
