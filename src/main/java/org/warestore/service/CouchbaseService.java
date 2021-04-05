package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.warestore.mapper.*;
import org.warestore.model.*;
import org.warestore.repository.*;
import org.warestore.service.enums.Categories;
import org.warestore.service.enums.Types;
import java.util.List;

@Log
@Service
public class CouchbaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WeaponRepository weaponRepository;
    @Autowired
    private AmmoRepository ammoRepository;
    @Autowired
    private TargetRepository targetRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public void updateCouchbaseFromDatabase(){
        log.warning("REWRITING COUCHBASE STARTS!");
        userRepository.deleteAll();
        List<User> users =  jdbcTemplate.query("select obj.id as id, param.attribute_id as atrrid, obj.name as username, usr.password as password, param.value from users usr, objects obj, parameters param \n" +
                        "where usr.object_id = obj.id and param.object_id = obj.id order by id, atrrid",
                new UserMapper());
        userRepository.saveAll(users);
        users.clear();
        log.info("All users added to couchbase.");

        orderRepository.deleteAll();
        List<Order> orders = jdbcTemplate.query("select obj.id, obj.name, obj2.name as product_name, obj3.name as username, attr.name as type, param.value " +
                "from objects obj, objects obj2, objects obj3, attributes attr, parameters param, links ls, links ls2 " +
                "where ls2.object_id = obj.id and obj2.id = ls2.reference_obj_id and ls2.type_id = 3 " +
                "and ls.object_id = obj.id and param.object_id = obj.id and param.attribute_id = attr.id " +
                "and obj.type_id = 5 and obj3.id = ls.reference_obj_id and obj3.type_id=1 order by id, type", new OrderMapper());
        orderRepository.saveAll(orders);
        orders.clear();
        log.info("All orders added to couchbase.");

        weaponRepository.deleteAll();
        saveWeaponType(Categories.RIFLES.ordinal(), new WeaponMapper(),"rifle");
        log.info("All rifles added to couchbase.");

        saveWeaponType(Categories.SHOTGUNS.ordinal(), new WeaponMapper(),"shotgun");
        log.info("All shotguns added to couchbase.");

        saveWeaponType(Categories.AIRGUNS.ordinal(), new WeaponMapper(), "airgun");
        log.info("All airguns added to couchbase.");

        ammoRepository.deleteAll();
        List<Ammo> ammo = (List<Ammo>) getCatalogData(Categories.AMMO.ordinal(), new AmmoMapper());
        ammoRepository.saveAll(ammo);
        ammo.clear();
        log.info("All ammo added to couchbase.");

        targetRepository.deleteAll();
        List<Target> targets = (List<Target>) getCatalogData(Categories.TARGETS.ordinal(), new TargetMapper());
        targetRepository.saveAll(targets);
        targets.clear();
        log.info("All targets added to couchbase.");

        categoryRepository.deleteAll();
        List<Category> categories = jdbcTemplate.query("select obj.name, param.value as url from objects obj, attributes attr, parameters param " +
                        "where param.object_id = obj.id and param.attribute_id = attr.id and obj.type_id = " + Types.CATEGORY.ordinal(),
                new CategoryMapper());
        categoryRepository.saveAll(categories);
        categories.clear();
        log.info("All categories added to couchbase.");
        log.warning("REWRITING COUCHBASE ENDS!");
    }

    private List<?> getCatalogData(int typeId, RowMapper<?> rowMapper){
        return jdbcTemplate.query("select obj.id, obj.name, attr.name as type, param.value from objects obj, attributes attr, parameters param\n" +
                "where param.object_id = obj.id and param.attribute_id = attr.id and obj.parent_id = "+typeId+
                "and attr.name !='product_type' order by id, type",rowMapper);
    }

    private void saveWeaponType(int typeId, RowMapper<?> rowMapper, String type){
        List<Weapon> weapons = (List<Weapon>) getCatalogData(typeId, rowMapper);
        for(Weapon rifle:weapons)
            rifle.setType(type);
        weaponRepository.saveAll(weapons);
    }
}
