package com.generator.main.generators;

import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class RandomWalker {
    float posX;
    float posY;
    int pixelsPerStep;
    Random rand;
    int facing;
    int possibleStepsPerMove;
    public RandomWalker(float startX, float startY, int pixelsPerStep, int possibleStepsPerMove) {
        this.posX = startX;
        this.posY = startY;
        this.pixelsPerStep = pixelsPerStep;
        this.possibleStepsPerMove = possibleStepsPerMove;
        rand = new Random();
        facing = 1;
    }


    /**
     * Causes the walker to move in a random direction as determined by its facing.
     * It takes a number of steps between 1 and the possibleStepsPerMove
     * Direction is represented as an int between 0 and 3 with 0 representing west.
     * The walker cannot walk backwards with this implementation.
     */
    public void move() {
        int steps = rand.nextInt(1, possibleStepsPerMove);
        switch (facing) {
            case 0:
                this.posX = this.posX - (this.pixelsPerStep * steps);
                facing = rand.nextInt(2);
                break;
            case 1:
                this.posY = this.posY + (this.pixelsPerStep * steps);
                facing = rand.nextInt(3);
                break;
            case 2:
                this.posX = this.posX + (this.pixelsPerStep * steps);
                facing = rand.nextInt(1,3);
                break;
        }

    }


    public void move(Float xLeft, Float xRight) throws Exception {
        float tempX = 0;
        int steps = rand.nextInt(1, possibleStepsPerMove);
        switch (facing) {
            case 0:
                tempX = this.posX - (this.pixelsPerStep * steps);
                if (tempX <= xLeft){
                    facing = 1;
                    throw new Exception("Walker out of bounds");
                }
                this.posX = tempX;
                facing = rand.nextInt(2);
                break;
            case 1:
                this.posY = this.posY + (this.pixelsPerStep * steps);
                facing = rand.nextInt(3);
                break;
            case 2:
                tempX = this.posX + (this.pixelsPerStep * steps);
                if (tempX >= xRight){
                    facing = 1;
                    throw new Exception("Walker out of bounds");
                }
                this.posX = tempX;
                facing = rand.nextInt(1,3);
                break;
        }
    }

    public void finalMove(){
        int steps = rand.nextInt(1, possibleStepsPerMove);
        this.posY = this.posY + (this.pixelsPerStep * steps);
    }

    public float[] getXY() {
        float[] output = {this.posX, this.posY};
        return output;
    }
}
