package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warestore.model.Ammo;
import org.warestore.model.Category;
import org.warestore.model.Target;
import org.warestore.model.Weapon;
import org.warestore.service.CatalogService;
import java.util.List;

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
}
