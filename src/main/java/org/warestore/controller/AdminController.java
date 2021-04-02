package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warestore.service.enums.AdminService;

@RestController
@RequestMapping(value = "/server/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping(value = "/get/order_page/{page}")
    public ResponseEntity<?> getOrders(@PathVariable int page){
        try{
            return adminService.getAllOrders(page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
