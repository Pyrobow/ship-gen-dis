package com.generator.main.experiments;

import java.util.Random;

public class Rectangle {

    static int MIN_SIZE = 5;
    static Random rnd = new Random();

    int top, left, width, height;
    Rectangle leftChild;
    Rectangle rightChild;
    Rectangle dungeon;

    public Rectangle(int top, int left, int height, int width) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public boolean split() {
        if( leftChild != null ) //if already split, bail out
            return false;
        boolean horizontal = rnd.nextBoolean(); //direction of split
        int max = (horizontal ? height : width ) - MIN_SIZE; //maximum height/width we can split off
        if( max <= MIN_SIZE ) // area too small to split, bail out
            return false;
        int split = rnd.nextInt( max ); // generate split point
        if( split < MIN_SIZE )  // adjust split point so there's at least MIN_SIZE in both partitions
            split = MIN_SIZE;
        if( horizontal ) { //populate child areas
            leftChild = new Rectangle( top, left, split, width );
            rightChild = new Rectangle( top+split, left, height-split, width );
        } else {
            leftChild = new Rectangle( top, left, height, split );
            rightChild = new Rectangle( top, left+split, height, width-split );
        }
        return true; //split successful
    }

    public void generateDungeon() {
        if( leftChild != null ) { //if current are has child areas, propagate the call
            leftChild.generateDungeon();
            rightChild.generateDungeon();
        } else { // if leaf node, create a dungeon within the minimum size constraints
            int dungeonTop = (height - MIN_SIZE <= 0) ? 0 : rnd.nextInt( height - MIN_SIZE);
            int dungeonLeft =  (width - MIN_SIZE <= 0) ? 0 : rnd.nextInt( width - MIN_SIZE);
            int dungeonHeight = Math.max(rnd.nextInt( height - dungeonTop ), MIN_SIZE );;
            int dungeonWidth = Math.max(rnd.nextInt( width - dungeonLeft ), MIN_SIZE );;
            dungeon = new Rectangle( top + dungeonTop, left+dungeonLeft, dungeonHeight, dungeonWidth);
        }
    }

}