package com.generator.main.objects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Map;

public class BaseComponent {

    private String name;
    private String description;
    private String componentType;
    private float tonnage;
    private int level;
    private boolean exteriorRequired;
    private ObjectMap<String, Integer> constraints;

    public BaseComponent(){}

    public BaseComponent(String name,
                         String description,
                         String componentType,
                         float tonnage,
                         int level,
                         boolean exteriorRequired,
                         ObjectMap<String, Integer> constraints){
        this.name = name;
        this.description = description;
        this.componentType = componentType;
        this.tonnage = tonnage;
        this.level = level;
        this.exteriorRequired = exteriorRequired;
        this.constraints = constraints;

    }

    public BaseComponent deepCopy(){
        return new BaseComponent(this.name,
                this.description,
                this.componentType,
                this.tonnage,
                this.level,
                this.exteriorRequired,
                this.constraints);
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

    public boolean isExteriorRequired() {
        return exteriorRequired;
    }

    public ObjectMap<String, Integer> getConstraints() {
        return constraints;
    }

    public int getLevel() {
        return level;
    }

    public String getComponentType(){
        return componentType;
    }

    public void setTonnage(float tonnage) {
        this.tonnage = tonnage;
    }
}
