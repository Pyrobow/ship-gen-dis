package com.generator.main.utils;

import com.generator.main.astar.Graph;
import com.generator.main.astar.TileNode;
import com.generator.main.enums.Direction;
import com.generator.main.enums.TileIdents;
import com.generator.main.objects.MapTile;
import com.generator.main.objects.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileMapToGraphConverter {
    public Graph<TileNode> convert(MapTile[][] baselayer){
        Set<TileNode> tileNodes = new HashSet<>();
        Map<String, Set<String>> connections = new HashMap<>();

        for (int y = 0; y < baselayer.length; y++){
            for (int x = 0; x < baselayer[0].length; x++){
                if (checkIfGoodNode(baselayer[y][x])){
                    String id = String.format("%d,%d", x, y);
                    Set<String> tempConnections = getConnectionSet(x, y, baselayer);
                    tileNodes.add(new TileNode(id, x, y));
                    connections.put(id, tempConnections);
                }

            }
        }
        return new Graph<>(tileNodes, connections);

    }

    private Set<String> getConnectionSet(int x, int y, MapTile[][] baselayer) {
        Set<String> output = new HashSet<>();
        for (Direction direction : Direction.values()){
            Pair<Integer, Integer> change = direction.getCoordChange();
            try {
                if (checkIfGoodNode(baselayer[y + change.first()][x + change.second()])){
                    String id = String.format("%d,%d", x + change.second(), y + change.first());
                    output.add(id);
                }
            }catch (IndexOutOfBoundsException exception){
                continue;
            }
        }
        return output;
    }

    private boolean checkIfGoodNode(MapTile mapTile) {
        return mapTile.getIdent() != TileIdents.EMPTY;
    }
}
