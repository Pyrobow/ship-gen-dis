package com.generator.main.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.generator.main.objects.*;
import com.github.tommyettinger.digital.Base;

import java.util.*;
import java.util.stream.Collectors;

public class RoleBasedSpecificationGenerator {
    private JsonReader reader;
    private JsonValue componentsJson;
    private JsonValue shipRolesJson;
    private Json json;
    private Random rand;
    private OrderedMap<String, ArrayList<BaseComponent>> componentsByType;
    private ArrayList<WeaponMountComponent> weaponMountComponents;
    private ArrayList<WeaponComponent> weaponComponents;
    private ArrayList<ShipRoleDefinition> shipRoleDefinitions;

    public RoleBasedSpecificationGenerator() {
        reader = new JsonReader();
        json = new Json();
        // Loads core components from file
        componentsJson = reader.parse(Gdx.files.internal("data/CoreComponents.json").reader());
        shipRolesJson = reader.parse(Gdx.files.internal("data/ShipRoles.json").reader());
        rand = new Random();
        componentsByType = jsonToBaseComponent(componentsJson);
        weaponMountComponents = jsonToWeaponMounts(componentsJson);
        weaponComponents = jsonToWeapons(componentsJson);
        shipRoleDefinitions = jsonToRoles(shipRolesJson);
    }

    public ShipSpecification createSpecification() {
        int hull = rand.nextInt(100, 10000);
        int usedHull = 0;
        int availableHardpoints = hull / 100;
        int usedHardPoints = 0;
        ShipRoleDefinition shipRoleDefinition = getShipRole();
        ShipSpecification specification = new ShipSpecification();
        OrderedMap<String, ArrayList<BaseComponent>> specificationComponents = new OrderedMap<>();
        ArrayList<WeaponMountComponent> specificationMountComponents = new ArrayList<>();
        for (String req : shipRoleDefinition.getRequiredComponents()) {
            if (req.toLowerCase().contains("weapon")) {
                ArrayList<WeaponMountComponent> selectedWeaponMounts = addWeapons(req, usedHull, hull, shipRoleDefinition, availableHardpoints);
                for (WeaponMountComponent mount : selectedWeaponMounts){
                    usedHull += mount.getTonnage();
                    usedHardPoints += mount.getHardpointsUsed();
                }
                specificationMountComponents.addAll(selectedWeaponMounts);
            }else if (req.equals("accommodation")){
                continue;
            }else {
                ArrayList<BaseComponent> selectedComponents = addComponents(req,
                        usedHull, hull, shipRoleDefinition);
                for (BaseComponent component : selectedComponents){
                    usedHull += component.getTonnage();
                }
                if (specificationComponents.containsKey(req.split("/")[0])){
                    specificationComponents.get(req.split("/")[0]).addAll(selectedComponents);
                }else{
                    specificationComponents.put(req.split("/")[0], selectedComponents);
                }
            }
            availableHardpoints = availableHardpoints - usedHardPoints;
        }
        for (WeaponMountComponent mount : specificationMountComponents){
            mount.setAssignedWeapons(new ArrayList<>());
            assignWeaponsToMounts(mount, weaponComponents);
        }
        int crew = calculateCrewNumber(specificationMountComponents, specificationComponents, shipRoleDefinition);
        ArrayList<BaseComponent> crewQuarters = createAccomodationArray(crew, usedHull, hull, shipRoleDefinition);
        for (BaseComponent quarter : crewQuarters){
            usedHull += quarter.getTonnage();
        }
        if (specificationComponents.containsKey("accommodation")){
            specificationComponents.get("accommodation").addAll(crewQuarters);
        }else{
            specificationComponents.put("accommodation", crewQuarters);
        }

        specification.setHullUsed(usedHull);
        specification.setTotalHull(hull);
        specification.setWeapons(specificationMountComponents);
        specification.setComponentsByType(specificationComponents);
        specification.setTotalCrew(crew);
        specification.setMilitaryShip(shipRoleDefinition.isMilitaryShip());
        specification.setSystemShip(specificationComponents.containsKey("jDrive"));

        return specification;
    }

    private ArrayList<BaseComponent> createAccomodationArray(int crew, int usedHull, int hull,
                                                             ShipRoleDefinition shipRoleDefinition) {
        ArrayList<BaseComponent> output = new ArrayList<>();
        boolean doubleBunking = false;
        BaseComponent standardAccomodation = componentsByType.get("accommodation").get(0);
        int defaultCrewTonnage = Math.round(crew * standardAccomodation.getTonnage());
        float doubleBunkingTonnage = standardAccomodation.getTonnage()/2;
        if (defaultCrewTonnage > (hull - usedHull) && shipRoleDefinition.isDoubleBunkingAllowed()){
            doubleBunking = true;
            standardAccomodation.setTonnage(doubleBunkingTonnage);
        }
        int quartersRequired = doubleBunking ? crew/2 : crew;
        for (int i = 0; i < quartersRequired; i++){
            output.add(standardAccomodation.deepCopy());
        }
        return output;

    }

    private ArrayList<BaseComponent> addComponents(String req,
                                                   int usedHull,
                                                   int hull,
                                                   ShipRoleDefinition shipRoleDefinition) {

        String[] reqs = req.split("/");
        List<BaseComponent> components = componentsByType.get(reqs[0]);
        ArrayList<BaseComponent> selectionList = new ArrayList<>();
        int minLevelRequirement = findMinLevelRequirement(req, shipRoleDefinition, reqs);
        float tonnageRequirement = findTonnageRequirement(req,hull,shipRoleDefinition,reqs);
        List<BaseComponent> useableComponents;
        if (reqs.length == 2){
            useableComponents = components.stream().filter(comp -> Objects.equals(comp.getName(), reqs[1]))
                    .collect(Collectors.toList());
        }else{
            useableComponents = components.stream().filter(comp -> comp.getLevel() >= minLevelRequirement &&
                    hullAvailable(usedHull, hull, comp)).collect(Collectors.toList());
        }
        if (useableComponents.size() != 0){
            if (tonnageRequirement == 0){
                BaseComponent selection = useableComponents.get(rand.nextInt(useableComponents.size())).deepCopy();
                if (selection.getTonnage() < 1){
                    selection.setTonnage(Math.max(Math.round(selection.getTonnage() * hull), 1));
                }
                selectionList.add(selection);
            }else{
                selectionList = createSelectionList(useableComponents, usedHull, hull, tonnageRequirement);
            }
        }
        return selectionList;
    }


    private ArrayList<WeaponMountComponent> addWeapons(String req,
                           int usedHull, int hull, ShipRoleDefinition shipRoleDefinition, int availableHardpoints) {
        String[] reqs = req.split("/");
        ArrayList<WeaponMountComponent> output = new ArrayList<>();
        int minLevelRequirement = findMinLevelRequirement(req, shipRoleDefinition, reqs);
        float tonnageRequirement = findTonnageRequirement(req, hull, shipRoleDefinition, reqs);
        List<WeaponMountComponent> useableWeaponMounts;
        if (reqs.length == 2){
            useableWeaponMounts = weaponMountComponents.stream().filter(weapon -> Objects.equals(weapon.getName(), reqs[1]) &&
                    hullAvailable(usedHull, hull, weapon)).collect(Collectors.toList());
        }else{
            useableWeaponMounts = weaponMountComponents.stream().filter(weapon -> hullAvailable(usedHull,hull,weapon) &&
                    availableHardpoints >= weapon.getHardpointsUsed() && weapon.getLevel() >= minLevelRequirement).collect(Collectors.toList());
        }
        if (useableWeaponMounts.size() != 0){
            if (tonnageRequirement == 0){
                WeaponMountComponent selection = useableWeaponMounts.get(rand.nextInt(useableWeaponMounts.size())).deepCopy();
                if (selection.getTonnage() < 1){
                    selection.setTonnage(Math.max(Math.round(selection.getTonnage() * hull), 1));
                }
                output.add(selection);
            }else{
                output = createWeaponMountOutput(availableHardpoints, usedHull, hull, tonnageRequirement, useableWeaponMounts);
            }
        }
        return output;

    }

    private ArrayList<WeaponMountComponent> createWeaponMountOutput(int availableHardpoints, int usedHull, int hull, float tonnageRequirement, List<WeaponMountComponent> useableWeaponMounts) {
        int selectedTonnage = 0;
        ArrayList<WeaponMountComponent> output = new ArrayList<>();
        int usedHardpoints = 0;
        while (selectedTonnage < tonnageRequirement){
            int finalUsedHull = usedHull;
            int finalUsedHardpoints = usedHardpoints;
            List<WeaponMountComponent> selectableComponents = useableWeaponMounts.stream()
                    .filter(m -> m.getHardpointsUsed() <= (availableHardpoints - finalUsedHardpoints)
                            && hullAvailable(finalUsedHull, hull, m)).collect(Collectors.toList());
            if (selectableComponents.isEmpty()){
                break;
            }
            WeaponMountComponent selection = selectableComponents.get(rand.nextInt(selectableComponents.size())).deepCopy();
            if (selection.getTonnage() < 1){
                selection.setTonnage(Math.max(Math.round(selection.getTonnage() * hull), 1));
            }
            selectedTonnage += selection.getTonnage();
            usedHardpoints += selection.getHardpointsUsed();
            usedHull += selection.getTonnage();
        }
        return output;
    }

    private ShipRoleDefinition getShipRole() {
        return shipRoleDefinitions.get(rand.nextInt(shipRoleDefinitions.size()));
    }

    private ArrayList<BaseComponent> createSelectionList(List<BaseComponent> useableComponents, int usedHull, int hull, float tonnageRequirement) {
        ArrayList<BaseComponent> selectionList = new ArrayList<>();
        int selectedTonnage = 0;
        int selectionFailure = 0;
        while (hull - usedHull > 0 && selectedTonnage < tonnageRequirement && selectionFailure < 3){
            BaseComponent selection = useableComponents.get(rand.nextInt(useableComponents.size())).deepCopy();
            if (selection.getTonnage() < 1){
                selection.setTonnage(Math.max(Math.round(selection.getTonnage() * hull), 1));
            }
            if (selection.getTonnage() > (hull - usedHull)){
                selectionFailure += 1;
            }else {
                usedHull += selection.getTonnage();
                selectedTonnage += selection.getTonnage();
                selectionList.add(selection);
            }
        }
        return selectionList;
    }

    private boolean hullAvailable(int usedHull, int hull, BaseComponent comp) {
        float actualTonnage;
        if (comp.getTonnage() < 1){
            actualTonnage = Math.round(Math.max(Math.round(comp.getTonnage() * hull), 1));
        }else {
            actualTonnage = comp.getTonnage();
        }
        return actualTonnage <= (hull - usedHull);
    }

    private int calculateCrewNumber(ArrayList<WeaponMountComponent> weapons,
                                    OrderedMap<String, ArrayList<BaseComponent>> componentsByType,
                                    ShipRoleDefinition role) {
        int totalCrew = 0;
        int pilots = 0;
        int sensorOperators = 0;
        int medics = 0;
        int captain = 0;
        if (role.isMilitaryShip()) {
            pilots += 3;
            sensorOperators = rand.nextInt(1, 4);
            captain = 1;
        } else {
            pilots = 1;
            sensorOperators = rand.nextInt(2);
            captain = rand.nextInt(2);
        }
        Iterator<String> typeIterator = componentsByType.keys();
        while (typeIterator.hasNext()) {
            for (BaseComponent room : componentsByType.get(typeIterator.next())) {
                if (room.getCrewTonnageRequirement().length == 2) {
                    int crewMultiplier = Math.round(room.getTonnage() / room.getCrewTonnageRequirement()[1]);
                    totalCrew += crewMultiplier * room.getCrewTonnageRequirement()[0];
                } else {
                    totalCrew += room.getStaticCrew();
                }
            }
        }
        for (WeaponMountComponent weapon : weapons) {
            if (weapon.getCrewTonnageRequirement().length == 2) {
                int crewMultiplier = Math.round(weapon.getTonnage() / weapon.getCrewTonnageRequirement()[1]);
                totalCrew += crewMultiplier * weapon.getCrewTonnageRequirement()[0];
            } else {
                totalCrew += weapon.getStaticCrew();
            }
        }
        totalCrew += (captain + sensorOperators + pilots);
        medics = totalCrew / 50;
        totalCrew += medics;
        return totalCrew;
    }

    private ArrayList<ShipRoleDefinition> jsonToRoles(JsonValue shipRolesJson) {
        ArrayList<ShipRoleDefinition> output = new ArrayList<>();
        for (JsonValue jsonValue : shipRolesJson.child()) {
            output.add(json.fromJson(ShipRoleDefinition.class, jsonValue.toString()));
        }
        return output;
    }

    private OrderedMap<String, ArrayList<BaseComponent>> jsonToBaseComponent(JsonValue componentsJson) {
        OrderedMap<String, ArrayList<BaseComponent>> output = new OrderedMap<String, ArrayList<BaseComponent>>();
        JsonValue.JsonIterator typeIterator = componentsJson.iterator();
        while (typeIterator.hasNext()) {
            ArrayList<BaseComponent> tempArray = new ArrayList<BaseComponent>();
            JsonValue byType = typeIterator.next();
            if (!byType.name().toLowerCase().contains("weapon")) {
                JsonValue.JsonIterator componentIterator = byType.iterator();
                while (componentIterator.hasNext()) {
                    tempArray.add(json.fromJson(BaseComponent.class, componentIterator.next().toString()));
                }
                output.put(byType.name, tempArray);
            }
        }
        return output;
    }

    private ArrayList<WeaponMountComponent> jsonToWeaponMounts(JsonValue componentsJson) {
        ArrayList<WeaponMountComponent> output = new ArrayList<WeaponMountComponent>();
        JsonValue weaponMountJson = componentsJson.get("weaponMount");
        JsonValue.JsonIterator weaponMountIterator = weaponMountJson.iterator();
        while (weaponMountIterator.hasNext()) {
            output.add(json.fromJson(WeaponMountComponent.class, weaponMountIterator.next().toString()));
        }
        return output;
    }

    private ArrayList<WeaponComponent> jsonToWeapons(JsonValue componentsJson) {
        ArrayList<WeaponComponent> output = new ArrayList<WeaponComponent>();
        JsonValue weaponJson = componentsJson.get("weapon");
        JsonValue.JsonIterator weaponIterator = weaponJson.iterator();
        while (weaponIterator.hasNext()) {
            output.add(json.fromJson(WeaponComponent.class, weaponIterator.next().toString()));
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
    private static float findTonnageRequirement(String req, int hull, ShipRoleDefinition shipRoleDefinition, String[] reqs) {
        float tonnageRequirement = 0;
        if (shipRoleDefinition.getTonnageRequirements().containsKey(req)){
            tonnageRequirement = shipRoleDefinition.getTonnageRequirements().get(req);
        } else if (shipRoleDefinition.getTonnageRequirements().containsKey(reqs[0])) {
            tonnageRequirement = shipRoleDefinition.getTonnageRequirements().get(reqs[0]);
        }
        if (tonnageRequirement < 1){
            tonnageRequirement = Math.max(Math.round(tonnageRequirement * hull), 1);
        }
        return tonnageRequirement;
    }

    private static int findMinLevelRequirement(String req, ShipRoleDefinition shipRoleDefinition, String[] reqs) {
        int minLevelRequirement;
        if (shipRoleDefinition.getMinLevelRequirements().containsKey(req)){
            minLevelRequirement = shipRoleDefinition.getMinLevelRequirements().get(req);
        } else if (shipRoleDefinition.getMinLevelRequirements().containsKey(reqs[0])) {
            minLevelRequirement = shipRoleDefinition.getMinLevelRequirements().get(reqs[0]);
        }else {
            minLevelRequirement = 0;
        }
        return minLevelRequirement;
    }
}
