package com.generator.main.objects;

import com.badlogic.gdx.utils.Array;

import java.util.Map;

public class WeaponComponent extends BaseComponent{

    private int hardpointsUsed;
    public WeaponComponent(String name,
                           String description,
                           String componentType,
                           int powerUsed,
                           float tonnage,
                           boolean exteriorRequired,
                           Array<Map<String, Integer>> constraints,
                           int hardpointsUsed) {
        super(name, description, componentType, powerUsed, tonnage, exteriorRequired, constraints);
        this.hardpointsUsed = hardpointsUsed;
    }
}
