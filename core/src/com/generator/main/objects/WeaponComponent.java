package com.generator.main.objects;

public class WeaponComponent {
    private String name;
    private String mountType;
    private int slotsUsed;

    public WeaponComponent(){}

    public WeaponComponent(String name, String mountType, int slotsUsed){
        this.name = name;
        this.mountType = mountType;
        this.slotsUsed = slotsUsed;
    }

    public WeaponComponent deepCopy(){
        return new WeaponComponent(this.name, this.mountType, this.slotsUsed);
    }

    public String getMountType() {
        return mountType;
    }

    public int getSlotsUsed() {
        return slotsUsed;
    }
}
