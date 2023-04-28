package com.generator.main.objects;

import com.badlogic.gdx.utils.OrderedMap;

public class ShipRoleDefinition {

    private String name;
    private String description;
    //Can be either a component type or a specific component.
    //If specific component format should be componentType/componentName
    private String[] requiredComponents;
    //Will only be added if room after all required components selected.
    private String[] additionalComponents;
    private OrderedMap<String, Integer> minLevelRequirements;
    //If tonnage < 0 treat as a percentage of hull.
    private OrderedMap<String, Float> tonnageRequirements;
    private boolean isMilitaryShip;
    private boolean isSystemShip;
    //If true the generator can treat half the tonnage of the accomdations (per person) in order to make it fit.
    private boolean isDoubleBunkingAllowed;

    public ShipRoleDefinition() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getRequiredComponents() {
        return requiredComponents;
    }

    public String[] getAdditionalComponents() {
        return additionalComponents;
    }

    public OrderedMap<String, Integer> getMinLevelRequirements() {
        return minLevelRequirements;
    }

    public OrderedMap<String, Float> getTonnageRequirements() {
        return tonnageRequirements;
    }

    public boolean isMilitaryShip() {
        return isMilitaryShip;
    }

    public boolean isSystemShip() {
        return isSystemShip;
    }

    public boolean isDoubleBunkingAllowed() {
        return isDoubleBunkingAllowed;
    }
}
