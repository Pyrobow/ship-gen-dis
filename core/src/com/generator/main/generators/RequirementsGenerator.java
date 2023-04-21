package com.generator.main.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.generator.main.objects.*;

import java.util.*;
import java.util.stream.Collectors;

public class RequirementsGenerator {
    private JsonReader reader;
    private JsonValue componentsJson;
    private Json json;
    private Random rand;
    private OrderedMap<String, ArrayList<BaseComponent>> componentsByType;
    private ArrayList<WeaponMountComponent> weaponMountComponents;
    private ArrayList<WeaponComponent> weaponComponents;

    public RequirementsGenerator(){
        reader = new JsonReader();
        json = new Json();
        // Loads core components from file
        componentsJson = reader.parse(Gdx.files.internal("data/CoreComponents.json").reader());
        rand = new Random();
        componentsByType = jsonToBaseComponent(componentsJson);
        weaponMountComponents = jsonToWeaponMounts(componentsJson);
        weaponComponents = jsonToWeapons(componentsJson);
    }

    public ShipSpecification createSpecification() {
        int hull = rand.nextInt(100, 10000);
        boolean systemShip = rand.nextBoolean();
        boolean militaryShip = rand.nextBoolean();
        int usedHull = 0;
        int plantAndDriveTonnage = 0;
        int turrets = 0;
        int bays = 0;
        int mainWeapons = 0;
        ShipSpecification output = new ShipSpecification();
        OrderedMap<String, ArrayList<BaseComponent>> componentsMap = buildEmptyMap(componentsJson.iterator());

        BaseComponent powerPlant = choosePowerPlant(componentsByType.get("powerPlant"), hull);
        usedHull += powerPlant.getTonnage();
        plantAndDriveTonnage += powerPlant.getTonnage();
        BaseComponent electronics = chooseElectronics(componentsByType.get("electronic"),
                hull- usedHull);
        usedHull += electronics.getTonnage();
        if (systemShip){
            BaseComponent mDrive = chooseDrive(componentsByType.get("mDrive"), powerPlant, hull);
            usedHull += mDrive.getTonnage();
            plantAndDriveTonnage += mDrive.getTonnage();
            componentsMap.get(mDrive.getComponentType()).add(mDrive);
        }else {
            BaseComponent jDrive = chooseDrive(componentsByType.get("jDrive"), powerPlant, hull);
            usedHull += jDrive.getTonnage();
            plantAndDriveTonnage += jDrive.getTonnage();
            int defaultFuelStorageTonnage = calculateDefaultFuelStorage(hull, jDrive, hull - usedHull);
            BaseComponent fuelStorage = componentsByType.get("storage").get(1).deepCopy();
            fuelStorage.setTonnage(defaultFuelStorageTonnage);
            usedHull += defaultFuelStorageTonnage;
            componentsMap.get(jDrive.getComponentType()).add(jDrive);
            componentsMap.get(fuelStorage.getComponentType()).add(fuelStorage);
        }
        BaseComponent bridge = chooseBridge(componentsByType.get("bridge"), hull, systemShip);
        usedHull += bridge.getTonnage();
        ArrayList<WeaponMountComponent> weapons = chooseWeaponMounts(weaponMountComponents, hull/100,
                hull - usedHull);
        for (WeaponMountComponent weapon : weapons){
            usedHull += weapon.getTonnage();
            if (weapon.getMountType().toLowerCase() == "turret"){
                turrets += 1;
            } else if (weapon.getMountType().toLowerCase() == "bay") {
                bays += 1;
            } else if (weapon.getMountType().toLowerCase() == "mainWeapon") {
                mainWeapons += 1;
            }
            weapon.setAssignedWeapons(new ArrayList<>());
            assignWeaponsToMounts(weapon, weaponComponents);
        }
        int crew = calculateCrewNumbers(militaryShip, plantAndDriveTonnage,turrets, bays,
                mainWeapons, electronics.getLevel());
        Pair<Integer, Boolean> stateRoomInformation = stateRoomTonnage(crew, hull - usedHull);
        usedHull += stateRoomInformation.first();
        BaseComponent cargo = componentsByType.get("storage").get(0).deepCopy();
        if (hull - usedHull > 0){
            cargo.setTonnage(hull - usedHull);
            usedHull = hull;
        }
        componentsMap.get(powerPlant.getComponentType()).add(powerPlant);
        componentsMap.get(bridge.getComponentType()).add(bridge);
        componentsMap.get(electronics.getComponentType()).add(electronics);

        output.setTotalHull(hull);
        output.setHullUsed(usedHull);
        output.setMilitaryShip(militaryShip);
        output.setSystemShip(systemShip);
        output.setTotalCrew(crew);
        output.setWeapons(weapons);
        output.setComponentsByType(componentsMap);

        return output;
    }

    private OrderedMap<String, ArrayList<BaseComponent>> jsonToBaseComponent(JsonValue componentsJson){
        OrderedMap<String, ArrayList<BaseComponent>> output = new OrderedMap<String, ArrayList<BaseComponent>>();
        JsonValue.JsonIterator typeIterator = componentsJson.iterator();
        while (typeIterator.hasNext()){
            ArrayList<BaseComponent> tempArray = new ArrayList<BaseComponent>();
            JsonValue byType = typeIterator.next();
            if (!byType.name().toLowerCase().contains("weapon")){
                JsonValue.JsonIterator componentIterator = byType.iterator();
                while (componentIterator.hasNext()){
                    tempArray.add(json.fromJson(BaseComponent.class, componentIterator.next().toString()));
                }
                output.put(byType.name, tempArray);
            }
        }
        return output;
    }

    private ArrayList<WeaponMountComponent> jsonToWeaponMounts(JsonValue componentsJson){
        ArrayList<WeaponMountComponent> output = new ArrayList<WeaponMountComponent>();
        JsonValue weaponMountJson = componentsJson.get("weaponMount");
        JsonValue.JsonIterator weaponMountIterator = weaponMountJson.iterator();
        while (weaponMountIterator.hasNext()){
            output.add(json.fromJson(WeaponMountComponent.class, weaponMountIterator.next().toString()));
        }
        return output;
    }

    private ArrayList<WeaponComponent> jsonToWeapons(JsonValue componentsJson){
        ArrayList<WeaponComponent> output = new ArrayList<WeaponComponent>();
        JsonValue weaponJson = componentsJson.get("weapon");
        JsonValue.JsonIterator weaponIterator = weaponJson.iterator();
        while (weaponIterator.hasNext()){
            output.add(json.fromJson(WeaponComponent.class, weaponIterator.next().toString()));
        }
        return output;
    }

    private OrderedMap<String, ArrayList<BaseComponent>> buildEmptyMap(JsonValue.JsonIterator typeiterator){
        OrderedMap<String, ArrayList<BaseComponent>> output = new OrderedMap<>();
        while (typeiterator.hasNext()){
            output.put(typeiterator.next().name, new ArrayList<BaseComponent>());
        }
        return output;
    }

    private BaseComponent choosePowerPlant(ArrayList<BaseComponent> powerPlants, int hull){
        BaseComponent powerPlant = powerPlants.get(rand.nextInt(powerPlants.size())).deepCopy();
        if (powerPlant.getTonnage() < 1){
            float percentage = powerPlant.getTonnage();
            powerPlant.setTonnage(percentage * hull);
        }
        return powerPlant;
    }

    private BaseComponent chooseDrive(ArrayList<BaseComponent> Drives, BaseComponent powerPlant, int hull){
        int maxLevel = powerPlant.getLevel();
        List<BaseComponent> availableJDrives = Drives.stream()
                .filter(d -> d.getLevel() <= maxLevel)
                .collect(Collectors.toList());
        BaseComponent drive = availableJDrives.get(rand.nextInt(availableJDrives.size()));
        if (drive.getTonnage() < 1){
            float percentage = powerPlant.getTonnage();
            powerPlant.setTonnage(percentage * hull);
        }
        return drive;
    }

    private int calculateDefaultFuelStorage(int hull, BaseComponent jDrive, int availableHull){
        return Math.min(Math.round(hull * 0.1f * jDrive.getLevel()), availableHull);
    }

    private BaseComponent chooseBridge(ArrayList<BaseComponent> bridges, int hull, boolean isSystemShip){
        BaseComponent bridge = bridges.get(rand.nextInt(bridges.size())).deepCopy();
        if (bridge.getTonnage() < 1){
            float actualTonnage = bridge.getTonnage() * hull;
            if (actualTonnage < 5 && isSystemShip){
                bridge.setTonnage(5);
            } else if (actualTonnage < 10 && !isSystemShip) {
                bridge.setTonnage(10);
            } else {
                bridge.setTonnage(actualTonnage);
            }
        }
        return bridge;
    }

    private BaseComponent chooseElectronics(ArrayList<BaseComponent> electronics,
                                            int availableHull) {
        List<BaseComponent> availableElectronics;
        availableElectronics = electronics.stream().filter(e -> e.getTonnage() <= availableHull)
                .collect(Collectors.toList());
        return availableElectronics.get(rand.nextInt(availableElectronics.size()));
    }

    private ArrayList<WeaponMountComponent> chooseWeaponMounts(ArrayList<WeaponMountComponent> weaponMounts,
                                                               int availableHardpoints,
                                                               int availableHull){
        ArrayList<WeaponMountComponent> output = new ArrayList<WeaponMountComponent>();
        while (availableHardpoints > 0){
            int finalAvailableHull = availableHull;
            int finalAvailableHardpoints = availableHardpoints;
            List<WeaponMountComponent> tempList = weaponMounts.stream()
                    .filter(m -> m.getHardpointsUsed() <= finalAvailableHardpoints
                    && m.getTonnage() <= finalAvailableHull).collect(Collectors.toList());
            if (tempList.size() == 0){
                return output;
            }
            WeaponMountComponent tempWeapon = tempList.get(rand.nextInt(tempList.size()));
            availableHardpoints = availableHardpoints - tempWeapon.getHardpointsUsed();
            availableHull = (int) (availableHull - tempWeapon.getTonnage());
            output.add(tempWeapon.deepCopy());
        }
        return output;
    }

    private void assignWeaponsToMounts(WeaponMountComponent weaponMount, ArrayList<WeaponComponent> weapons){
        int availableSlots = weaponMount.getWeaponSlots();
        while (availableSlots > 0){
            int finalAvailableSlots = availableSlots;
            List<WeaponComponent> tempList = weapons.stream().filter(w ->
                    w.getMountType().toLowerCase().equals(weaponMount.getMountType().toLowerCase())
                            && w.getSlotsUsed() <= finalAvailableSlots).collect(Collectors.toList());
            WeaponComponent tempWeap = tempList.get(rand.nextInt(tempList.size()));
            availableSlots = availableSlots - tempWeap.getSlotsUsed();
            weaponMount.updateAssignedWeapons(tempWeap.deepCopy());
        }
    }

    private BaseComponent chooseShield(ArrayList<BaseComponent> shields, int availableHull) throws Exception {
        List<BaseComponent> availableShields = shields.stream().filter(s -> s.getTonnage() <= availableHull)
                .collect(Collectors.toList());
        if (availableShields.size() == 0){
            throw new Exception("Shield choice failed");
        }
        return availableShields.get(rand.nextInt(availableShields.size()));
    }

    private int calculateCrewNumbers(boolean isMilitary,
                                     int driveAndPlantTonnage,
                                     int turrets,
                                     int bays,
                                     int mainGuns,
                                     int sensorLevel){

        int crewFromDrivesAndPlant = driveAndPlantTonnage/35;
        int pilots;
        int sensorOperators;
        int medics;
        int gunners;
        int captain;

        if (isMilitary){
            pilots = 3;
            sensorOperators = rand.nextInt(1,4);
            captain = 1;
        }else{
            pilots = 1;
            sensorOperators = rand.nextInt(2);
            captain = rand.nextInt(2);
        }
        gunners = (turrets - (sensorLevel -1 )) + bays*2 + mainGuns*10;
        int totalCrew = pilots + sensorOperators + gunners + captain + crewFromDrivesAndPlant;
        medics = totalCrew/50;
        totalCrew += medics;

        return totalCrew;
    }

    private Pair<Integer, Boolean> stateRoomTonnage(int crew, int availableHull){
        Integer tonnage = crew*4;
        Boolean doubleBunking = false;
        if (tonnage > availableHull){
            tonnage = tonnage/2;
            doubleBunking = true;
        }
        Pair<Integer, Boolean> output = new Pair<>(tonnage, doubleBunking);
        return output;
    }

}
