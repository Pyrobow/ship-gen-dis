package com.generator.main.generators;

import com.badlogic.gdx.math.Polygon;

import java.util.ArrayList;
import java.util.Random;

public class HullGenerator {
    private int minArea;
    private float marginSize;
    private int targetArea;
    private int sideLength;
    private int possibleStepsPerMove;
    private Random rand = new Random();


    public HullGenerator(int minArea, float marginSize, int possibleStepsPerMove){
        this.minArea = minArea;
        this.marginSize = marginSize;
        this.possibleStepsPerMove = possibleStepsPerMove;
        targetArea = Math.round(minArea + (minArea * marginSize));
        sideLength = (int) (Math.sqrt(targetArea));
    }

    public Polygon generateSymmetricHull() throws Exception {
        int errorCount = 0;
        boolean finished = false;
        Polygon output = new Polygon();
        ArrayList<Float> leftPath = new ArrayList<Float>();
        ArrayList<Float> rightPath = new ArrayList<Float>();
        RandomWalker leftWalker = new RandomWalker(rand.nextInt(0, sideLength/2),
                0,
                possibleStepsPerMove);
        leftPath.add(leftWalker.getXY()[0]);
        leftPath.add(leftWalker.getXY()[1]);
        rightPath.add(sideLength - (leftWalker.getXY()[0]));
        rightPath.add(leftWalker.getXY()[1]);
        while (!(finished)){
            for (int i = 0; i<5; i++){
                try {
                    leftWalker.move(0, sideLength/2);
                    errorCount = 0;
                    leftPath.add(leftWalker.getXY()[0]);
                    leftPath.add(leftWalker.getXY()[1]);
                    rightPath.add(sideLength - (leftWalker.getXY()[0]));
                    rightPath.add(leftWalker.getXY()[1]);
                } catch (Exception e){
                    if (errorCount == 3){
                        throw new Exception("Hull generation failed");
                    }
                    errorCount += 1;
                }
            }
            output = new Polygon(concatenateAndConvertToArrayLeftAndRightPaths(leftPath, flipVertexArray(rightPath)));
            if (Math.abs(output.area()) >= targetArea){
                leftWalker.finalMove();
                leftPath.add(leftWalker.getXY()[0]);
                leftPath.add(leftWalker.getXY()[1]);
                rightPath.add(sideLength - (leftWalker.getXY()[0]));
                rightPath.add(leftWalker.getXY()[1]);
                finished = true;
            }
            output = new Polygon(concatenateAndConvertToArrayLeftAndRightPaths(leftPath, flipVertexArray(rightPath)));
        }
        return output;
    }

    public float[] concatenateAndConvertToArrayLeftAndRightPaths(ArrayList<Float> leftPath, ArrayList<Float> rightPath){
        float[] output = new float[(leftPath.size() + rightPath.size())];
        for (int i = 0; i < leftPath.size(); i++){
            output[i] = leftPath.get(i);
        }
        for (int i = 0; i < rightPath.size(); i++){
            output[leftPath.size() + i] = rightPath.get(i);
        }
        return output;
    }

    private ArrayList<Float> flipVertexArray(ArrayList<Float> vertexList){
        ArrayList<Float> output = new ArrayList<Float>(vertexList.size());
        for (int i = vertexList.size()-1; i >= 0; i = i-2){
            output.add(vertexList.get(i-1));
            output.add(vertexList.get(i));
        }
        return output;
    }


}
