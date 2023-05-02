package com.generator.main.objects;

import com.badlogic.gdx.graphics.Color;
import com.generator.main.enums.Direction;
import com.generator.main.enums.GrowthTypes;
import com.generator.main.enums.TileIdents;

import java.util.ArrayList;
import java.util.Objects;

public class GrowthFrontArray {

    private ArrayList<Pair<Integer, Integer>> pointsInFront;
    private Direction direction;
    private ArrayList<GrowthFrontArray> orthogonalFronts;
    private BaseComponent room;

    public GrowthFrontArray(Direction direction, ArrayList<Pair<Integer, Integer>> pointsInFront, BaseComponent room){
        this.pointsInFront = pointsInFront;
        this.direction = direction;
        this.room = room;
        orthogonalFronts = new ArrayList<>();
    }

    public void populateOrthogonalFronts(ArrayList<GrowthFrontArray> frontArrays){
        for (GrowthFrontArray front : frontArrays){
            if (checkIfFrontOrthogonal(front)){
                orthogonalFronts.add(front);
            }
        }

    }

    public boolean checkIfFrontOrthogonal(GrowthFrontArray front){
        if (front.getDirection().isOrthogonal(direction)){
            for (Pair<Integer, Integer> comparisonFrontCoord : front.getPointsInFront()){
                for (Pair<Integer, Integer> thisFrontCoord : pointsInFront){
                    if (comparisonFrontCoord.pairEquals(thisFrontCoord)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void growFrontOneStepForward(MapTile[][] baselayer){
        ArrayList<Pair<Integer, Integer>> newPoints = new ArrayList<>();
        Color tempColor = new Color(0,255,0,1);
        Pair<Integer, Integer> directionChange = direction.getCoordChange();
        for (Pair<Integer, Integer> coord : pointsInFront){
            newPoints.add(new Pair<>(coord.first() + directionChange.first(),
                    coord.second() + directionChange.second()));
            baselayer[coord.first() + directionChange.first()][coord.second() + directionChange.second()].setAssignedComponent(room);
            baselayer[coord.first() + directionChange.first()][coord.second() + directionChange.second()].setColour(room.getColor());
            baselayer[coord.first() + directionChange.first()][coord.second() + directionChange.second()].setIdent(TileIdents.ROOM);
        }
        updateOrthogonalFronts(directionChange);
        pointsInFront = newPoints;
    }

    public boolean isFrontValid(MapTile[][] baseLayer){
        Pair<Integer, Integer> directionChange = direction.getCoordChange();
        for (Pair<Integer, Integer> point : pointsInFront){
            int newY =  point.first() + directionChange.first();
            int newX = point.second() + directionChange.second();
            if (checkInBounds(newY, newX, baseLayer)){
                if (baseLayer[newY][newX].getIdent() != TileIdents.HULL) {
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    private void updateOrthogonalFronts(Pair<Integer, Integer> directionChange) {
        ArrayList<GrowthFrontArray> nonOrthogonalFronts = new ArrayList<>();
        for (GrowthFrontArray frontArray : orthogonalFronts){
            try {
                Pair<Integer, Integer> common = getCommonOrthogonalPoint(frontArray);
                Pair<Integer, Integer> newPoint = new Pair<>(common.first() + directionChange.first(),
                        common.second() + directionChange.second());
                frontArray.getPointsInFront().add(newPoint);
            }catch (Exception e){
                if (Objects.equals(e.getMessage(), "Front not orthogonal")){
                    nonOrthogonalFronts.add(frontArray);
                } else {
                    System.out.print(e);
                }
            }
        }
        if (nonOrthogonalFronts.size() > 0){
            for (GrowthFrontArray frontArray : nonOrthogonalFronts){
                orthogonalFronts.remove(frontArray);
            }
        }
    }

    public Pair<Integer, Integer> getCommonOrthogonalPoint(GrowthFrontArray frontArray) throws Exception {
        if (!checkIfFrontOrthogonal(frontArray)){
            throw new Exception("Front not orthogonal");
        }
        for (Pair<Integer, Integer> comparisonFrontCoord : frontArray.getPointsInFront()){
            for (Pair<Integer, Integer> thisFrontCoord : pointsInFront){
                if (comparisonFrontCoord.pairEquals(thisFrontCoord)){
                    return thisFrontCoord;
                }
            }
        }
        return null;
    }

    private boolean checkInBounds(int y, int x, MapTile[][] baseLayer) {
        if ((y >= 0) && (y < baseLayer.length)) {
            return (x >= 0) && (x < baseLayer[0].length);
        } else {
            return false;
        }
    }

    public ArrayList<Pair<Integer, Integer>> getPointsInFront() {
        return pointsInFront;
    }

    public Direction getDirection() {
        return direction;
    }

    public ArrayList<GrowthFrontArray> getOrthogonalFronts() {
        return orthogonalFronts;
    }
}
