package com.generator.main.objects;

import com.badlogic.gdx.utils.OrderedMap;
import jdk.internal.net.http.common.Pair;

import java.util.ArrayList;
import java.util.Map;

public class ShipSpecification {
    private OrderedMap<String, ArrayList<BaseComponent>> componentsByType;
    private ArrayList<WeaponMountComponent> weapons;
    private int totalCrew;
    private int totalHull;
    private int hullUsed;
    private boolean systemShip;
    private boolean militaryShip;

    public ShipSpecification(){}

    public ArrayList<WeaponMountComponent> getWeapons() {
        return weapons;
    }

    public void setWeapons(ArrayList<WeaponMountComponent> weapons) {
        this.weapons = weapons;
    }

    public int getTotalCrew() {
        return totalCrew;
    }

    public void setTotalCrew(int totalCrew) {
        this.totalCrew = totalCrew;
    }

    public int getTotalHull() {
        return totalHull;
    }

    public void setTotalHull(int totalHull) {
        this.totalHull = totalHull;
    }

    public int getHullUsed() {
        return hullUsed;
    }

    public void setHullUsed(int hullUsed) {
        this.hullUsed = hullUsed;
    }

    public boolean isSystemShip() {
        return systemShip;
    }

    public void setSystemShip(boolean systemShip) {
        this.systemShip = systemShip;
    }

    public boolean isMilitaryShip() {
        return militaryShip;
    }

    public void setMilitaryShip(boolean militaryShip) {
        this.militaryShip = militaryShip;
    }

    public OrderedMap<String, ArrayList<BaseComponent>> getComponentsByType() {
        return componentsByType;
    }

    public void setComponentsByType(OrderedMap<String, ArrayList<BaseComponent>> componentsByType) {
        this.componentsByType = componentsByType;
    }
}
