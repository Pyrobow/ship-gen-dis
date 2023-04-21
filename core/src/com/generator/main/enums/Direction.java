package com.generator.main.enums;

import com.generator.main.objects.Pair;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public boolean isOrthogonal(Direction comparison){
       switch (this){
           case NORTH:
           case SOUTH:
               if (comparison == Direction.NORTH || comparison == Direction.SOUTH){
                   return false;
               }
               break;
           case EAST:
           case WEST:
               if (comparison == Direction.EAST || comparison == Direction.WEST){
                   return false;
               }
               break;
       }
       return true;
    }

    public Pair<Integer, Integer> getCoordChange(){
        switch (this){
            case NORTH:
                return new Pair<>(-1, 0);
            case EAST:
                return new Pair<>(0, 1);
            case SOUTH:
                return new Pair<>(1, 0);
            case WEST:
                return new Pair<>(0, -1);
        }
        return null;
    }

}
