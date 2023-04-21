package com.generator.main.generators;

import com.badlogic.gdx.utils.OrderedMap;
import com.generator.main.objects.BaseComponent;
import com.generator.main.objects.MapTile;
import com.generator.main.objects.Pair;
import com.generator.main.objects.ShipSpecification;

import java.util.ArrayList;
import java.util.Iterator;

public class RoomGrowthManager {
    ShipSpecification specification;
    ArrayList<RoomGrower> ongoingGrowth;
    ArrayList<RoomGrower> finishedGrowth;
    MapTile[][] baselayer;
    private OrderedMap<String, ArrayList<BaseComponent>> componentsByType;

    public RoomGrowthManager(ShipSpecification specification, MapTile[][] baselayer) throws Exception {
        this.baselayer = baselayer;
        this.specification = specification;
        componentsByType = specification.getComponentsByType();
        ongoingGrowth = createNewGrowers();
        finishedGrowth = new ArrayList<>();

    }

    private ArrayList<RoomGrower> createNewGrowers() throws Exception {
        ArrayList<RoomGrower> output = new ArrayList<>();
        Iterator<String> typeIterator = componentsByType.keys();
        while (typeIterator.hasNext()){
            Iterator<BaseComponent> roomIter = componentsByType.get(typeIterator.next()).iterator();
            while (roomIter.hasNext()){
                BaseComponent room = roomIter.next();
                output.add(new RoomGrower(room, baselayer));
            }
        }
        return output;
    }

    public void growAllRooms(){
        while (!ongoingGrowth.isEmpty()){
            for (RoomGrower grower : ongoingGrowth){
                if (grower.isFinished()){
                    finishedGrowth.add(grower);
                }else {
                    grower.growRoom();
                }
            }
            for (RoomGrower grower : finishedGrowth){
                if (ongoingGrowth.contains(grower)){
                    ongoingGrowth.remove(grower);
                }
            }
        }
    }
}
