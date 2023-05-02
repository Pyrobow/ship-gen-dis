package com.generator.main.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;

public class BaseComponent {

    private String name;
    private String description;
    private String componentType;
    private float tonnage;
    private int level;
    private boolean exteriorRequired;
    private ObjectMap<String, Integer> constraints;
    private int[] crewTonnageRequirement;
    private int staticCrew;
    private Color color;

    public BaseComponent() {
    }

    public BaseComponent(String name,
                         String description,
                         String componentType,
                         float tonnage,
                         int level,
                         boolean exteriorRequired,
                         ObjectMap<String, Integer> constraints,
                         int[] crewPerTon,
                         int staticCrew,
                         Color color) {
        this.name = name;
        this.description = description;
        this.componentType = componentType;
        this.tonnage = tonnage;
        this.level = level;
        this.exteriorRequired = exteriorRequired;
        this.constraints = constraints;
        this.crewTonnageRequirement = crewPerTon;
        this.staticCrew = staticCrew;
        this.color = color;

    }

    public BaseComponent deepCopy() {
        return new BaseComponent(this.name,
                this.description,
                this.componentType,
                this.tonnage,
                this.level,
                this.exteriorRequired,
                this.constraints,
                this.crewTonnageRequirement,
                this.staticCrew,
                this.color);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getTonnage() {
        return tonnage;
    }

    public void setTonnage(float tonnage) {
        this.tonnage = tonnage;
    }

    public boolean isExteriorRequired() {
        return exteriorRequired;
    }

    public ObjectMap<String, Integer> getConstraints() {
        return constraints;
    }

    public int getLevel() {
        return level;
    }

    public int[] getCrewTonnageRequirement() {
        return crewTonnageRequirement;
    }

    public int getStaticCrew() {
        return staticCrew;
    }

    public Color getColor() {
        return color;
    }

    public String getComponentType() {
        return componentType;
    }
}
