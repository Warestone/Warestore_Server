package org.warestore.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.warestore.mapper.AmmoMapper;
import org.warestore.mapper.CategoryMapper;
import org.warestore.mapper.TargetMapper;
import org.warestore.mapper.WeaponMapper;
import org.warestore.model.object.Ammo;
import org.warestore.model.object.Category;
import org.warestore.model.object.Target;
import org.warestore.model.object.Weapon;
import org.warestore.service.enums.Categories;
import org.warestore.service.enums.Types;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CatalogService {
    private final Logger logger = Logger.getLogger(PostService.class.getName());
    private final static String GET_WEAPON_OR_AMMO_QUERY = "select obj.id, obj.name, attr.name as type, param.value from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and attr.name !='product_type' and obj.parent_id = ";
    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_1 = "select obj.id, obj.name, attr.name as type, param.value from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.parent_id = ";
    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_2 = " and attr.name !='product_type' " +
            "limit 20 offset ";
    private final static String GET_CATEGORY_QUERY = "select obj.name, param.value as url from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.type_id = ";



    public List<Category> getCategories(JdbcTemplate jdbcTemplate){
        logger.info("Return categories.");
        if (!DataStorage.getCategoriesList().isEmpty()) return DataStorage.getCategoriesList();
        else{
           List<Category> categoriesList = jdbcTemplate.query(GET_CATEGORY_QUERY + Types.CATEGORY.ordinal(),
                   new CategoryMapper());
            DataStorage.setCategoriesList(categoriesList);
        }
       return DataStorage.getCategoriesList();
    }


    public List<Weapon> getRifles(JdbcTemplate jdbcTemplate){
        logger.info("Return all rifles.");
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_QUERY+ Categories.RIFLES.ordinal(), new WeaponMapper());
    }
    public List<Weapon> getShotguns(JdbcTemplate jdbcTemplate){
        logger.info("Return shotguns.");
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_QUERY+Categories.SHOTGUNS.ordinal(), new WeaponMapper());
    }
    public List<Weapon> getAirguns(JdbcTemplate jdbcTemplate){
        logger.info("Return airguns.");
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_QUERY+Categories.AIRGUNS.ordinal(), new WeaponMapper());
    }
    public List<Ammo> getAmmo(JdbcTemplate jdbcTemplate){
        logger.info("Return ammo.");
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_QUERY+Categories.AMMO.ordinal(), new AmmoMapper());
    }
    public List<Target> getTargets(JdbcTemplate jdbcTemplate){
        logger.info("Return targets.");
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_QUERY+Categories.TARGETS.ordinal(), new TargetMapper());
    }


    public List<Weapon> getRiflesPage(JdbcTemplate jdbcTemplate, int page){
        logger.info("Return rifles page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.RIFLES.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new WeaponMapper());
    }
    public List<Weapon> getShotgunsPage(JdbcTemplate jdbcTemplate, int page){
        logger.info("Return shotguns page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.SHOTGUNS.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new WeaponMapper());
    }
    public List<Weapon> getAirgunsPage(JdbcTemplate jdbcTemplate, int page){
        logger.info("Return airguns page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.AIRGUNS.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new WeaponMapper());
    }
    public List<Ammo> getAmmoPage(JdbcTemplate jdbcTemplate, int page){
        logger.info("Return ammo page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.AMMO.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new AmmoMapper());
    }
    public List<Target> getTargetPage(JdbcTemplate jdbcTemplate, int page){
        logger.info("Return ammo page "+page);
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_1+Categories.TARGETS.ordinal()+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_2+page*20, new TargetMapper());
    }
}
