package org.warestore.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.model.*;
import org.warestore.repository.CategoryRepository;
import org.warestore.repository.OrderRepository;
import org.warestore.service.enums.Attributes;
import org.warestore.service.enums.Types;
import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private Cluster cluster;
    @Autowired
    private OrderRepository orderRepository;


    public ResponseEntity<List<Category>> getCategories(){
        List<Category> categories = categoryRepository.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    public ResponseEntity<?> getRiflesPage(int page){
        log.info("Return rifles page "+page);
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Weapon' and type = 'rifle' limit 5 offset "+page*5);
        List<Weapon> rifles = queryResult.rowsAs(Weapon.class);
        return new ResponseEntity<>(rifles,HttpStatus.OK);
    }
    public ResponseEntity<?> getShotgunsPage(int page){
        log.info("Return shotguns page "+page);
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Weapon' and type = 'shotgun' limit 5 offset "+page*5);
        List<Weapon> shotguns = queryResult.rowsAs(Weapon.class);
        return new ResponseEntity<>(shotguns,HttpStatus.OK);
    }
    public ResponseEntity<?> getAirgunsPage(int page){
        log.info("Return airguns page "+page);
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Weapon' and type = 'airgun' limit 5 offset "+page*5);
        List<Weapon> airguns = queryResult.rowsAs(Weapon.class);
        return new ResponseEntity<>(airguns,HttpStatus.OK);
    }
    public ResponseEntity<?> getAmmoPage(int page){
        log.info("Return ammo page "+page);
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Ammo' limit 5 offset "+page*5);
        List<Ammo> ammo = queryResult.rowsAs(Ammo.class);
        return new ResponseEntity<>(ammo,HttpStatus.OK);
    }
    public ResponseEntity<?> getTargetPage(int page){
        log.info("Return target page "+page);
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Target' limit 5 offset "+page*5);
        List<Target> targets = queryResult.rowsAs(Target.class);
        return new ResponseEntity<>(targets,HttpStatus.OK);
    }

    public ResponseEntity<?> createOrder(HashMap<Integer, Item> cart, HttpServletRequest request){
        ResponseEntity<?> response = userService.getUserByName(
                jwtProvider.getUsernameFromToken(jwtProvider.getTokenFromRequest(request)));
        if (response.getStatusCode()!=HttpStatus.NOT_FOUND){
            User user = (User) response.getBody();
            assert user != null;
            for(Item item:cart.values()){ // check quantity of products in cart
                Product product = getProduct(item.getId()).get(0);
                if (product==null)
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                else if (product.getQuantity()<item.getQuantity())
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                item.setQtyInWarehouse(product.getQuantity());
            }
            String orderNo="";
            for(Item item:cart.values())
                orderNo = insertOrderAndUpdateProductQuantity(item,user);

            log.info("Sending message (order) to user '"+user.getUsername()+"'.");
            mailService.sendMessage(
                    user.getEmail(),"Заказ WARESTORE "+orderNo,
                    mailService.compileOrderMessage(cart, user));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private List<Product> getProduct(int id){
        return cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where meta(d).id ='"+id+"'").rowsAs(Product.class);
    }

    @Transactional
    protected String insertOrderAndUpdateProductQuantity(Item item, User user){
        //get new order name ('№' + last object with type = 'order' + 1)
        String nameOrder = "№"+jdbcTemplate.queryForObject("select count(id)+1 as current_order from objects where type_id = 5", String.class);
        jdbcTemplate.update("insert into objects (name, type_id) values ('"+nameOrder+"',"+Types.ORDER.ordinal()+")");

        String date = new SimpleDateFormat("yyyy.MM.dd ',' hh:mm:ss a").format(new Date());

        String idOrder = jdbcTemplate.queryForObject("select id from objects where name = '"+nameOrder+"'",String.class);
        jdbcTemplate.update("insert into parameters (object_id, attribute_id, value) values" +
                "("+idOrder+","+Attributes.QUANTITY.ordinal()+",'"+item.getQuantity()+"')," +
                "("+idOrder+","+Attributes.PRICE.ordinal()+",'"+item.getTotalPrice()+"')," +
                "("+idOrder+","+(Attributes.DATE.ordinal()+2)+",'"+date+"')," +
                "("+idOrder+","+(Attributes.STATUS.ordinal()+2)+",'оплачен')");

        //update product quantity in database
        jdbcTemplate.update("update parameters set value = '"+(item.getQtyInWarehouse()-item.getQuantity())+
                "' where object_id = "+item.getId()+" and attribute_id = "+Attributes.QUANTITY.ordinal());

        //links order-user, order-product (one order - one product)
        jdbcTemplate.update("insert into links (object_id, reference_obj_id, type_id) values" +
                "("+idOrder+","+user.getId()+","+Types.USER.ordinal()+"),"+
                "("+idOrder+","+item.getId()+","+Types.ITEM.ordinal()+")"
        );

        //update quantity in couchbase
        cluster.query("UPDATE warestore SET quantity = "+(item.getQtyInWarehouse()-item.getQuantity())+" WHERE meta(warestore).id = '"+item.getId()+"'");

        //get new order in couchbase
        Order order = new Order();
        order.setNameOrder(nameOrder);
        order.setQuantity(item.getQuantity());
        order.setName(item.getName());
        order.setPrice(item.getTotalPrice());
        order.setDate(date);
        order.setStatus("оплачен");
        order.setUsername(user.getUsername());
        order.setId(Integer.parseInt(idOrder));
        orderRepository.save(order);

        return nameOrder;
    }
}
