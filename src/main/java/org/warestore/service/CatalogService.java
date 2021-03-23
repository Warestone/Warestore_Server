package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.mapper.*;
import org.warestore.model.*;
import org.warestore.service.enums.Categories;
import org.warestore.service.enums.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Log
@Service
public class CatalogService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MailService mailService;

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

    public ResponseEntity<?> createOrder(HashMap<Integer, Item> cart, Token token){
        ResponseEntity<?> response = userService.getUserByName(
                jwtProvider.getUsernameFromToken(token.getTokenWithoutBearer()));
        if (response.getStatusCode()!=HttpStatus.NOT_FOUND){
            User user = (User) response.getBody();
            for(Item item:cart.values()){ // check quantity of products in cart
                Product product = getProduct(item.getId()).get(0);
                if (product==null)
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                else if (product.getQuantity()<item.getQuantity())
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            for(Item item:cart.values()) {
                //make order
                //update product quantity
                //УТОЧНИТЬ - как связывать с юзером: по id в objects
            }
            assert user != null;
            log.info("Sending message (order) to user '"+user.getUsername()+"'.");
            //getOrderId(user.getId()) в ЗАКАЗ
            mailService.sendMessage(
                    "jontimofeev@yandex.ru","Заказ WARESTORE № 1",
                    mailService.compileMessage(cart, user));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private List<?> getData(int page, int typeId, RowMapper rowMapper, int limit){
        return jdbcTemplate.query(GET_WEAPON_OR_AMMO_PAGE_QUERY_PART1 +typeId+
                GET_WEAPON_OR_AMMO_PAGE_QUERY_PART2 +limit+" offset "+limit*page, rowMapper);
    }

    private List<Product> getProduct(int id){
        return jdbcTemplate.query("select obj.id, obj.name, attr.name as type, param.value from objects obj, attributes attr, parameters param\n" +
                "where param.object_id = obj.id and param.attribute_id = attr.id and attr.name !='product_type' and obj.id = "+id +" order by id", new ProductMapper());
    }

    private int getOrderId(int userId){
        return jdbcTemplate.queryForObject("",Integer.class);
    }
}
