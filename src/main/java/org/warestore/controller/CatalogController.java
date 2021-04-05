package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getCategories(){
        try {
            return catalogService.getCategories();
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/get/rifle_page/{page}")
    public ResponseEntity<?> getRiflesPage(@PathVariable int page){
        try{
            return catalogService.getRiflesPage(page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/get/shotgun_page/{page}")
    public ResponseEntity<?> getShotgunsPage(@PathVariable int page){
        try{
            return catalogService.getShotgunsPage(page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/get/airgun_page/{page}")
    public ResponseEntity<?> getAirgunsPage(@PathVariable int page){
        try{
            return catalogService.getAirgunsPage(page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/get/ammo_page/{page}")
    public ResponseEntity<?> getAmmoPage(@PathVariable int page){
        try{
            return catalogService.getAmmoPage(page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    }

    @GetMapping(value = "/get/target_page/{page}")
    public ResponseEntity<?> getTargetsPage(@PathVariable int page){
        try{
            return catalogService.getTargetPage(page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/post/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> makeNewOrder(@RequestBody HashMap<Integer, Item> cart, HttpServletRequest request){
        try{
            return catalogService.createOrder(cart, request);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
