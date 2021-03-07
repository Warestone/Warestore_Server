package org.warestore.service;

import org.springframework.stereotype.Component;
import org.warestore.model.Ammo;
import org.warestore.model.Category;
import org.warestore.model.Target;
import org.warestore.model.Weapon;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataStorage {
    private static  List<Category> categoriesList = new ArrayList<>();
    private static List<Weapon> riflesList = new ArrayList<>();
    private static List<Weapon> shotgunsList = new ArrayList<>();
    private static List<Weapon> airgunsList = new ArrayList<>();
    private static List<Ammo> ammoList = new ArrayList<>();
    private static List<Target> targetList = new ArrayList<>();

    public static List<Category> getCategoriesList() { return categoriesList; }

    public static List<Weapon> getRiflesList() { return riflesList; }

    public static List<Weapon> getShotgunsList() { return shotgunsList; }

    public static List<Weapon> getAirgunsList() { return airgunsList; }

    public static List<Ammo> getAmmoList() { return ammoList; }

    public static List<Target> getTargetList() { return targetList; }

    public static void setCategoriesList(List<Category> categoriesList) { DataStorage.categoriesList = categoriesList; }

    public static void setRiflesList(List<Weapon> riflesList) { DataStorage.riflesList = riflesList; }

    public static void setShotgunsList(List<Weapon> shotgunsList) { DataStorage.shotgunsList = shotgunsList; }

    public static void setAirgunsList(List<Weapon> airgunsList) { DataStorage.airgunsList = airgunsList; }

    public static void setAmmoList(List<Ammo> ammoList) { DataStorage.ammoList = ammoList; }

    public static void setTargetList(List<Target> targetList) { DataStorage.targetList = targetList; }
}
