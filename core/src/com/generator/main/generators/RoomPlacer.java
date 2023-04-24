package com.generator.main.generators;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.OrderedMap;
import com.generator.main.enums.TileIdents;
import com.generator.main.objects.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class RoomPlacer {
    private final MapTile[][] baseLayer;
    private final OrderedMap<String, HeatMap> heatMaps;
    private final ShipSpecification shipSpec;
    private final OrderedMap<String, ArrayList<BaseComponent>> componentsByType;
    private final ArrayList<WeaponMountComponent> armaments;
    private final Random rand;
    final private int DROPOFF = 5;

    public RoomPlacer(MapTile[][] baseLayer, ShipSpecification shipSpec){
        this.baseLayer = baseLayer;
        this.shipSpec = shipSpec;
        rand = new Random();
        componentsByType = shipSpec.getComponentsByType();
        armaments = shipSpec.getWeapons();
        heatMaps = createEmptyHeatmaps();
    }

    public void placeAllRooms(){
        Iterator<String> typeIterator = componentsByType.keys();
        while (typeIterator.hasNext()){
            for (BaseComponent room : componentsByType.get(typeIterator.next())) {
                Pair<Integer, Integer> chosen = placeRoom(heatMaps.get(String.format("%s/%s", room.getComponentType(),
                        room.getName())), room);
                updateAllHeatmaps(room, chosen.second(), chosen.first(), DROPOFF);
            }
        }
        for (WeaponMountComponent weapon : armaments){
            String key = String.format("%s/%s", weapon.getMountType(), weapon.getName());
            Pair<Integer, Integer> chosen = placeRoom(heatMaps.get(key), weapon);
            updateAllHeatmaps(weapon, chosen.second(), chosen.first(), DROPOFF);
        }
    }

    public Pair<Integer, Integer> placeRoom(HeatMap heatMap, BaseComponent component){
        int highest = -5000;
        ArrayList<Pair<Integer,Integer>> possibleLocations = new ArrayList<>();
        for (int i = 0; i < heatMap.getHeatmap().length; i++){
            for (int j = 0; j < heatMap.getHeatmap()[i].length; j++){
                if (heatMap.getHeatmap()[i][j] > highest){
                    highest = heatMap.getHeatmap()[i][j];
                    possibleLocations.clear();
                    possibleLocations.add(new Pair<>(i, j));
                } else if (heatMap.getHeatmap()[i][j] == highest) {
                    possibleLocations.add(new Pair<>(i, j));
                }
            }
        }

        Pair<Integer, Integer> chosen = possibleLocations.get(rand.nextInt(0, possibleLocations.size()));
        baseLayer[chosen.first()][chosen.second()].setAssignedComponent(component);
        baseLayer[chosen.first()][chosen.second()].setIdent(TileIdents.ROOM);
        baseLayer[chosen.first()][chosen.second()].setColour(new Color(255,255,0, 1));
        return chosen;
    }

    public void updateAllHeatmaps(BaseComponent placedComponent, int centreX, int centreY, int dropoff){
        Iterator<String> typeIterator = heatMaps.keys();
        while (typeIterator.hasNext()){
            updateHeatmap(heatMaps.get(typeIterator.next()), placedComponent, centreX, centreY, dropoff);
        }
    }

    public void updateHeatmap(HeatMap heatMap, BaseComponent placedComponent, int centreX, int centreY, int dropoff){
        heatMap.getHeatmap()[centreY][centreX] = -5000;
        if (heatMap.getConstraints().containsKey(placedComponent.getComponentType())){
            Integer weight = heatMap.getConstraints().get(placedComponent.getComponentType());
            if (weight < 0){
                dropoff = dropoff * -1;
            }
            for(int i = 0; i < heatMap.getHeatmap().length; i++){
                for (int j = 0; j < heatMap.getHeatmap()[0].length; j++){
                    int change = weight - dropoff*calculateManhattan(centreX, centreY, j, i);
                    if (Math.abs(change) > 0){
                        heatMap.getHeatmap()[i][j] += change;
                    }
                }
            }
        }
    }

    public OrderedMap<String, HeatMap> createEmptyHeatmaps(){
        OrderedMap<String, HeatMap> output = new OrderedMap<>();
        Iterator<String> typeIterator = componentsByType.keys();
        while (typeIterator.hasNext()){
            for (BaseComponent tempComponent : componentsByType.get(typeIterator.next())) {
                String key = String.format("%s/%s", tempComponent.getComponentType(), tempComponent.getName());
                if (!output.containsKey(key)) {
                    output.put(key, createBlankHeatMap(tempComponent.isExteriorRequired(), tempComponent));
                }
            }
        }
        for (WeaponMountComponent weapon : armaments){
            String key = String.format("%s/%s", weapon.getMountType(), weapon.getName());
            if (!output.containsKey(key)) {
                output.put(key, createBlankHeatMap(weapon.isExteriorRequired(), weapon));
            }
        }
        return output;
    }

    private HeatMap createBlankHeatMap(Boolean edgeRequired, BaseComponent component) {
        int[][] heatmapArray = new int[baseLayer.length][baseLayer[0].length];
        for (int i = 0; i < baseLayer.length; i++){
            for (int j = 0; j < baseLayer[0].length; j++){
                if (baseLayer[i][j].getIdent() == TileIdents.EMPTY){
                    heatmapArray[i][j] = -5000;
                }else{
                    if (edgeRequired){
                        //Checks to see if the point is on the edge of the array.
                        if (i == 0 || i == baseLayer.length - 1 || j == 0 || j == baseLayer[0].length - 1){
                            heatmapArray[i][j] = 0;
                        } else if (Objects.equals(baseLayer[i + 1][j].getIdent(), TileIdents.EMPTY) || Objects.equals(baseLayer[i - 1][j].getIdent(), TileIdents.EMPTY)
                                || Objects.equals(baseLayer[i][j + 1].getIdent(), TileIdents.EMPTY) || Objects.equals(baseLayer[i][j - 1].getIdent(), TileIdents.EMPTY)) {
                            heatmapArray[i][j] = 0;
                        }else{
                            heatmapArray[i][j] = -5000;
                        }
                    }else{
                        heatmapArray[i][j] = 0;
                    }
                }
            }
        }
        return new HeatMap(heatmapArray, component.getConstraints());
    }

    private int calculateManhattan(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
