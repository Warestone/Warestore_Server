package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_PART1 = "select obj.id, obj.name, attr.name as type, param.value from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.parent_id = ";
    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_PART2 = " and attr.name !='product_type' " +
            "limit ";
    private final static String GET_CATEGORY_QUERY = "select obj.name, param.value as url from objects obj, attributes attr, parameters param\n" +
            "where param.object_id = obj.id and param.attribute_id = attr.id and obj.type_id = ";


    public ResponseEntity<List<Category>> getCategories(){
        log.info("Return categories.");
        if (!DataStorage.getCategoriesList().isEmpty())
            return new ResponseEntity<>(DataStorage.getCategoriesList(), HttpStatus.OK);
        else{
           List<Category> categoriesList = jdbcTemplate.query(GET_CATEGORY_QUERY + Types.CATEGORY.ordinal(),
                   new CategoryMapper());
            DataStorage.setCategoriesList(categoriesList);
        }
       return new ResponseEntity<>(DataStorage.getCategoriesList(), HttpStatus.OK);
    }

    public ResponseEntity<?> getRiflesPage(int page){
        log.info("Return rifles page "+page);
        return new ResponseEntity<>((List<Weapon>) getData(page,Categories.RIFLES.ordinal(),
                new WeaponMapper(),20), HttpStatus.OK);
    }
    public ResponseEntity<?> getShotgunsPage(int page){
        log.info("Return shotguns page "+page);
        return new ResponseEntity<> ((List<Weapon>) getData(page,Categories.SHOTGUNS.ordinal(),
                new WeaponMapper(),20),HttpStatus.OK);
    }
    public ResponseEntity<?> getAirgunsPage(int page){
        log.info("Return airguns page "+page);
        return new ResponseEntity<> ((List<Weapon>) getData(page,Categories.AIRGUNS.ordinal(),
                new WeaponMapper(),20),HttpStatus.OK);
    }
    public ResponseEntity<?> getAmmoPage(int page){
        log.info("Return ammo page "+page);
        return new ResponseEntity<> ((List<Ammo>) getData(page,Categories.AMMO.ordinal(),
                new AmmoMapper(),25),HttpStatus.OK);
    }
    public ResponseEntity<?> getTargetPage(int page){
        log.info("Return ammo page "+page);
        return new ResponseEntity<> ((List<Target>) getData(page,Categories.TARGETS.ordinal(),
                new TargetMapper(),20), HttpStatus.OK);
    }

    private List<?> getData(int page, int typeId, RowMapper rowMapper, int limit){
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_PART1 +typeId+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_PART2 +limit+" offset "+limit*page, rowMapper);
    }
}
