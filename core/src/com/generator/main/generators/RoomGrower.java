package com.generator.main.generators;

import com.generator.main.enums.Direction;
import com.generator.main.enums.GrowthTypes;
import com.generator.main.objects.*;

import java.util.ArrayList;
import java.util.Random;

public class RoomGrower {
    BaseComponent room;
    MapTile[][] baseLayer;
    ArrayList<GrowthFrontArray> validGrowthFronts;
    ArrayList<GrowthFrontArray> invalidGrowthFronts;
    Pair<Integer, Integer> startPoint;
    GrowthTypes growthType;
    int size;
    int minSize;
    boolean finished;
    Random rand;

    public RoomGrower(BaseComponent room, MapTile[][] baseLayer) throws Exception {
        this.room = room;
        this.baseLayer = baseLayer;
        startPoint = findCentre(room, baseLayer);
        validGrowthFronts = createInitialGrowthFronts(startPoint);
        growthType = GrowthTypes.SQUARE;
        finished = false;
        size = 1;
        minSize = (int) room.getTonnage();
        rand = new Random();
        invalidGrowthFronts = new ArrayList<>();
        for (GrowthFrontArray front : validGrowthFronts) {
            if (!front.isFrontValid(baseLayer)) {
                invalidGrowthFronts.add(front);
            }
        }
        for (GrowthFrontArray front : invalidGrowthFronts) {
            validGrowthFronts.remove(front);
        }
    }

    public Pair<Integer, Integer> getStartPoint() {
        return startPoint;
    }

    public void growRoom() {
        updateGrowthFrontValidity();
        switch (growthType) {
            case SQUARE:
                growRoomRectangle();
                break;
            case LSHAPE:
                finished = true;
                break;
        }
        checkMinGrowthFinished();
    }

    private void checkMinGrowthFinished() {
        if (size >= minSize) {
            finished = true;
        }
    }

    private void updateGrowthFrontValidity() {
        for (GrowthFrontArray front : validGrowthFronts) {
            if (!front.isFrontValid(baseLayer)) {
                invalidGrowthFronts.add(front);
            }
        }
        for (GrowthFrontArray front : invalidGrowthFronts) {
            validGrowthFronts.remove(front);
        }
    }

    private void growRoomRectangle() {
        if (validGrowthFronts.isEmpty()) {
            growthType = GrowthTypes.LSHAPE;
        } else {
            int selection = rand.nextInt(0, validGrowthFronts.size());
            validGrowthFronts.get(selection).growFrontOneStepForward(baseLayer);
            size = size + calculateSizeIncrease(validGrowthFronts.get(selection));
        }
    }

    private int calculateSizeIncrease(GrowthFrontArray growthFront) {
        return growthFront.getPointsInFront().size();
    }


    private ArrayList<GrowthFrontArray> createInitialGrowthFronts(Pair<Integer, Integer> centre) {
        ArrayList<GrowthFrontArray> output = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            ArrayList<Pair<Integer, Integer>> initialPoints = new ArrayList<>();
            initialPoints.add(new Pair<>(centre.first(), centre.second()));
            GrowthFrontArray tempFront = new GrowthFrontArray(direction, initialPoints, room);
            output.add(tempFront);
        }
        for (GrowthFrontArray front : output) {
            for (GrowthFrontArray orthFront : output) {
                if (front.checkIfFrontOrthogonal(orthFront)) {
                    front.getOrthogonalFronts().add(orthFront);
                }
            }
        }
        return output;
    }

    private Pair<Integer, Integer> findCentre(BaseComponent room, MapTile[][] baseLayer) throws Exception {
        Pair<Integer, Integer> output = new Pair<>(null, null);
        for (int i = 0; i < baseLayer.length; i++) {
            for (int j = 0; j < baseLayer[0].length; j++) {
                if (baseLayer[i][j].getAssignedComponent() == room) {
                    output = new Pair<>(i, j);
                }
            }
        }
        if (output.first() == null) {
            throw new Exception("Cannot find room centre.");
        }
        return output;
    }

    public boolean isFinished() {
        return finished;
    }


}
