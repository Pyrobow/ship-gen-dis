package com.generator.main.objects;

import com.badlogic.gdx.utils.Array;

import java.util.Map;

public class PowerGenerationComponent extends BaseComponent{

    private  int powerPerTon;

    public PowerGenerationComponent(String name,
                                    String description,
                                    String componentType,
                                    int powerUsed,
                                    float tonnage,
                                    boolean exteriorRequired,
                                    Array<Map<String, Integer>> constraints,
                                    int powerPerTon) {
        super(name, description, componentType, powerUsed, tonnage, exteriorRequired, constraints);

        this.powerPerTon = powerPerTon;
    }

    public int getPowerPerTon() {
        return powerPerTon;
    }
}
