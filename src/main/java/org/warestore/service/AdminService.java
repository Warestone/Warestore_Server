package org.warestore.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.warestore.model.Order;

import java.util.List;

@Log
@Service
public class AdminService {
    @Autowired
    private Cluster cluster;

    public ResponseEntity<?> getAllOrders(int page){
        log.info("Return admin page "+page);
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Order' limit 5 offset "+page*5);
        List<Order> orders = queryResult.rowsAs(Order.class);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
