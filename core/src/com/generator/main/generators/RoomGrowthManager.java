package com.generator.main.generators;

import com.badlogic.gdx.utils.OrderedMap;
import com.generator.main.objects.*;

import java.util.ArrayList;
import java.util.Iterator;

public class RoomGrowthManager {
    ShipSpecification specification;
    ArrayList<RoomGrower> ongoingGrowth;
    ArrayList<RoomGrower> finishedGrowth;
    MapTile[][] baselayer;
    private final OrderedMap<String, ArrayList<BaseComponent>> componentsByType;
    private final ArrayList<WeaponMountComponent> armaments;

    public RoomGrowthManager(ShipSpecification specification, MapTile[][] baselayer) throws Exception {
        this.baselayer = baselayer;
        this.specification = specification;
        componentsByType = specification.getComponentsByType();
        armaments = specification.getWeapons();
        ongoingGrowth = createNewGrowers();
        finishedGrowth = new ArrayList<>();

    }

    private ArrayList<RoomGrower> createNewGrowers() throws Exception {
        ArrayList<RoomGrower> output = new ArrayList<>();
        Iterator<String> typeIterator = componentsByType.keys();
        while (typeIterator.hasNext()){
            for (BaseComponent room : componentsByType.get(typeIterator.next())) {
                output.add(new RoomGrower(room, baselayer));
            }
        }
        for (WeaponMountComponent weapon : armaments){
            output.add(new RoomGrower(weapon, baselayer));
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
                ongoingGrowth.remove(grower);
            }
        }
    }
}
