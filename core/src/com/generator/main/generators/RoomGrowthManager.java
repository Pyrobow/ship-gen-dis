package com.generator.main.generators;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.OrderedMap;
import com.generator.main.astar.Graph;
import com.generator.main.astar.ManhattanScorer;
import com.generator.main.astar.RouteFinder;
import com.generator.main.astar.TileNode;
import com.generator.main.enums.TileIdents;
import com.generator.main.objects.*;
import com.generator.main.utils.TileMapToGraphConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    public void placeAllCorridors(){
        TileMapToGraphConverter converter = new TileMapToGraphConverter();
        Graph<TileNode> layerGraph = converter.convert(baselayer);
        RouteFinder<TileNode> finder = new RouteFinder<>(layerGraph, new ManhattanScorer(), new ManhattanScorer());
        Collections.shuffle(finishedGrowth);
        for (int i = 0; i < finishedGrowth.size() - 1; i++){
            Pair<Integer, Integer> to = finishedGrowth.get(i).getStartPoint();
            Pair<Integer, Integer> from = finishedGrowth.get(i + 1).getStartPoint();
            List<TileNode> route = finder.findRoute(layerGraph.getNode(String.format("%d,%d",to.second(), to.first())
            ), layerGraph.getNode(String.format("%d,%d",from.second(), from.first())));
            for (TileNode node : route){
                updateRender(node);
            }
        }

    }

    private void updateRender(TileNode node) {
        Color corridorColor = new Color(0, 255, 251, 1);
        if (baselayer[node.getY()][node.getX()].getIdent() == TileIdents.HULL){
            baselayer[node.getY()][node.getX()].setColour(corridorColor);
            baselayer[node.getY()][node.getX()].setIdent(TileIdents.CORRIDOR);
        }
    }
}
