package com.generator.main.objects;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;

public class WeaponMountComponent {

    private String name;
    private String description;
    private String componentType;
    private float tonnage;
    private int level;
    private boolean exteriorRequired;
    private ObjectMap<String, Integer> constraints;
    private int hardpointsUsed;
    private ArrayList<WeaponComponent> assignedWeapons;
    private int weaponSlots;
    private String mountType;

    public WeaponMountComponent(){}

    public WeaponMountComponent(String name,
                                String description,
                                String componentType,
                                float tonnage,
                                int level,
                                boolean exteriorRequired,
                                ObjectMap<String, Integer> constraints,
                                int hardpointsUsed,
                                ArrayList<WeaponComponent> assignedWeapons,
                                int weaponSlots,
                                String mountType){
        this.name = name;
        this.description = description;
        this.componentType = componentType;
        this.tonnage = tonnage;
        this.level = level;
        this.exteriorRequired = exteriorRequired;
        this.constraints = constraints;
        this.hardpointsUsed = hardpointsUsed;
        this.assignedWeapons = assignedWeapons;
        this.weaponSlots = weaponSlots;
        this.mountType = mountType;
    }

    public WeaponMountComponent deepCopy(){
        return new WeaponMountComponent(name,
                description,
                componentType,
                tonnage,
                level,
                exteriorRequired,
                constraints,
                hardpointsUsed,
                assignedWeapons,
                weaponSlots,
                mountType);
    }

    public int getHardpointsUsed() {
        return hardpointsUsed;
    }

    public String getMountType(){
        return mountType;
    }

    public int getWeaponSlots(){
        return weaponSlots;
    }

    public float getTonnage() {
        return tonnage;
    }

    public void setAssignedWeapons(ArrayList<WeaponComponent> assignedWeapons) {
        this.assignedWeapons = assignedWeapons;
    }

    public void updateAssignedWeapons(WeaponComponent weapon){
        this.assignedWeapons.add(weapon);
    }
}
