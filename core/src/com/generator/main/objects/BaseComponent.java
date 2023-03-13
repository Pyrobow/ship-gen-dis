package com.generator.main.objects;

import com.badlogic.gdx.utils.Array;
import java.util.Map;

public class BaseComponent {

    private String name;
    private String description;
    private String componentType;
    private int powerUsed;
    private float tonnage;
    private boolean exteriorRequired;
    private Array<Map<String, Integer>> constraints;

    public BaseComponent(String name,
                         String description,
                         String componentType,
                         int powerUsed,
                         float tonnage,
                         boolean exteriorRequired,
                         Array<Map<String, Integer>> constraints) {
        this.name = name;
        this.description = description;
        this.componentType = componentType;
        this.powerUsed = powerUsed;
        this.tonnage = tonnage;
        this.exteriorRequired = exteriorRequired;
        this.constraints = constraints;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPowerUsed() {
        return powerUsed;
    }

    public float getTonnage() {
        return tonnage;
    }

    public boolean isExteriorRequired() {
        return exteriorRequired;
    }

    public Array<Map<String, Integer>> getConstraints() {
        return constraints;
    }
}
