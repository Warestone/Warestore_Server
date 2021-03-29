package org.warestore.service.enums;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.warestore.mapper.OrderMapper;
import org.warestore.model.Order;

import java.util.List;

@Log
@Service
public class AdminService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ResponseEntity<?> getAllOrders(int page){
        log.info("Return admin page "+page);
        List<Order> orders = jdbcTemplate.query("select obj.id, obj.name, obj2.name as product_name, obj3.name as username, attr.name as type, param.value " +
                "from objects obj, objects obj2, objects obj3, attributes attr, parameters param, links ls, links ls2 " +
                "where ls2.object_id = obj.id and obj2.id = ls2.reference_obj_id and ls2.type_id = 3 " +
                "and ls.object_id = obj.id and param.object_id = obj.id and param.attribute_id = attr.id " +
                "and obj.type_id = 5 and obj3.id = ls.reference_obj_id and obj3.type_id=1 order by id, type limit 20 offset "+20*page, new OrderMapper());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
