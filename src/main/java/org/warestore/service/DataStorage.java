package org.warestore.service;

import org.springframework.stereotype.Component;
import org.warestore.model.Category;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataStorage {
    private static  List<Category> categoriesList = new ArrayList<>();

    public static List<Category> getCategoriesList() { return categoriesList; }

    public static void setCategoriesList(List<Category> categoriesList) { DataStorage.categoriesList = categoriesList; }
}
