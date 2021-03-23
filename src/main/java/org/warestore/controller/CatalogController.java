package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.warestore.model.*;
import org.warestore.service.CatalogService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/server/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping(value = "/get/category")
    public ResponseEntity<?> getCategories(){ return catalogService.getCategories(); }

    @GetMapping(value = "/get/rifle_page/{id}")
    public ResponseEntity<?> getRiflesPage(@PathVariable int id){ return catalogService.getRiflesPage(id); }

    @GetMapping(value = "/get/shotgun_page/{id}")
    public ResponseEntity<?> getShotgunsPage(@PathVariable int id){ return catalogService.getShotgunsPage(id); }

    @GetMapping(value = "/get/airgun_page/{id}")
    public ResponseEntity<?> getAirgunsPage(@PathVariable int id){ return catalogService.getAirgunsPage(id); }

    @GetMapping(value = "/get/ammo_page/{id}")
    public ResponseEntity<?> getAmmoPage(@PathVariable int id){ return catalogService.getAmmoPage(id); }

    @GetMapping(value = "/get/target_page/{id}")
    public ResponseEntity<?> getTargetsPage(@PathVariable int id){ return catalogService.getTargetPage(id); }

    @PostMapping(value = "/post/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> makeNewOrder(@RequestBody HashMap<Integer, Item> cart, HttpServletRequest request){
        Token token = new Token(request.getHeader("Authorization"));
        return catalogService.createOrder(cart, token);
    }
}
