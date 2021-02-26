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




    public List<Category> getCategories(JdbcTemplate jdbcTemplate){
        logger.info("Return categories.");
        if (!DataStorage.getCategoriesList().isEmpty()) return DataStorage.getCategoriesList();
        else{
           List<Category> categoriesList = jdbcTemplate.query(
                    "select obj.name, param.value as url from objects obj, attributes attr, parameters param\n" +
                            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.type_id = "+ Types.CATEGORY.ordinal(),
                    new CategoryMapper());
            DataStorage.setCategoriesList(categoriesList);
        }
       return DataStorage.getCategoriesList();
    }

    public List<Weapon> getRifles(JdbcTemplate jdbcTemplate){
        logger.info("Return rifles.");
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
}
