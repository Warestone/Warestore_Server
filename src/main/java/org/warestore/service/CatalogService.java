package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.warestore.mapper.AmmoMapper;
import org.warestore.mapper.CategoryMapper;
import org.warestore.mapper.TargetMapper;
import org.warestore.mapper.WeaponMapper;
import org.warestore.model.Ammo;
import org.warestore.model.Category;
import org.warestore.model.Target;
import org.warestore.model.Weapon;
import org.warestore.service.enums.Categories;
import org.warestore.service.enums.Types;
import java.util.List;

@Log
@Service
public class CatalogService {

    public CatalogService(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    private final JdbcTemplate jdbcTemplate;

    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_1 = "select obj.id, obj.name, attr.name as type, param.value from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.parent_id = ";
    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_2 = " and attr.name !='product_type' " +
            "limit 20 offset ";
    private final static String GET_CATEGORY_QUERY = "select obj.name, param.value as url from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.type_id = ";


    public List<Category> getCategories(){
        log.info("Return categories.");
        if (!DataStorage.getCategoriesList().isEmpty()) return DataStorage.getCategoriesList();
        else{
           List<Category> categoriesList = jdbcTemplate.query(GET_CATEGORY_QUERY + Types.CATEGORY.ordinal(),
                   new CategoryMapper());
            DataStorage.setCategoriesList(categoriesList);
        }
       return DataStorage.getCategoriesList();
    }

    public List<Weapon> getRiflesPage(int page){
        log.info("Return rifles page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.RIFLES.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new WeaponMapper());
    }
    public List<Weapon> getShotgunsPage(int page){
        log.info("Return shotguns page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.SHOTGUNS.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new WeaponMapper());
    }
    public List<Weapon> getAirgunsPage(int page){
        log.info("Return airguns page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.AIRGUNS.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new WeaponMapper());
    }
    public List<Ammo> getAmmoPage(int page){
        log.info("Return ammo page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.AMMO.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new AmmoMapper());
    }
    public List<Target> getTargetPage(int page){
        log.info("Return ammo page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.TARGETS.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new TargetMapper());
    }
}
