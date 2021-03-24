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
import org.warestore.service.enums.Attributes;
import org.warestore.service.enums.Categories;
import org.warestore.service.enums.Types;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    private final static String GET_WEAPON_OR_AMMO_PAGE_QUERY_PART2 = " and attr.name !='product_type' order by id, type limit ";
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
        log.info("Return target page "+page);
        return new ResponseEntity<> ((List<Target>) getData(page,Categories.TARGETS.ordinal(),
                new TargetMapper(),20), HttpStatus.OK);
    }

    public ResponseEntity<?> createOrder(HashMap<Integer, Item> cart, Token token){
        ResponseEntity<?> response = userService.getUserByName(
                jwtProvider.getUsernameFromToken(token.getTokenWithoutBearer()));
        if (response.getStatusCode()!=HttpStatus.NOT_FOUND){
            User user = (User) response.getBody();
            assert user != null;
            for(Item item:cart.values()){ // check quantity of products in cart
                Product product = getProduct(item.getId()).get(0);
                if (product==null)
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                else if (product.getQuantity()<item.getQuantity())
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                item.setQtyInWarehouse(product.getQuantity());
            }
            String orderNo="";
            for(Item item:cart.values())
                orderNo = insertOrderAndUpdateProductQuantity(item,user);

            log.info("Sending message (order) to user '"+user.getUsername()+"'.");
            mailService.sendMessage(
                    "jontimofeev@yandex.ru","Заказ WARESTORE "+orderNo,
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
                "where param.object_id = obj.id and param.attribute_id = attr.id and attr.name in('price','quantity') and obj.id = "+id +" order by id, type", new ProductMapper());
    }

    @Transactional //annotation not working
    protected String insertOrderAndUpdateProductQuantity(Item item, User user){
        //get new order name ('№' + last object with type = 'order' + 1)
        String nameOrder = "№"+jdbcTemplate.queryForObject("select count(id)+1 as current_order from objects where type_id = 5", String.class);
        jdbcTemplate.update("insert into objects (name, type_id) values ('"+nameOrder+"',"+Types.ORDER.ordinal()+")");

        String idOrder = jdbcTemplate.queryForObject("select id from objects where name = '"+nameOrder+"'",String.class);
        jdbcTemplate.update("insert into parameters (object_id, attribute_id, value) values" +
                "("+idOrder+","+Attributes.QUANTITY.ordinal()+",'"+item.getQuantity()+"')," +
                "("+idOrder+","+Attributes.PRICE.ordinal()+",'"+item.getTotalPrice()+"')," +
                "("+idOrder+","+(Attributes.DATE.ordinal()+2)+",'"+new SimpleDateFormat("yyyy.MM.dd ',' hh:mm:ss a").format(new Date())+"')," +
                "("+idOrder+","+(Attributes.STATUS.ordinal()+2)+",'оплачен')");

        //update product quantity
        jdbcTemplate.update("update parameters set value = '"+(item.getQtyInWarehouse()-item.getQuantity())+
                "' where object_id = "+item.getId()+" and attribute_id = "+Attributes.QUANTITY.ordinal());

        //links order-user, order-product (one order - one product)
        jdbcTemplate.update("insert into links (object_id, reference_obj_id, type_id) values" +
                "("+idOrder+","+user.getId()+","+Types.USER.ordinal()+"),"+
                "("+idOrder+","+item.getId()+","+Types.ITEM.ordinal()+")"
        );

        return nameOrder;
    }
}
