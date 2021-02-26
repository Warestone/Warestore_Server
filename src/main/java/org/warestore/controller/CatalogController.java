package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warestore.model.object.Ammo;
import org.warestore.model.object.Category;
import org.warestore.model.object.Target;
import org.warestore.model.object.Weapon;
import org.warestore.service.CatalogService;

import java.util.List;

@RestController
@RequestMapping(value = "/server/catalog")
public class CatalogController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final CatalogService catalogService = new CatalogService();

    @GetMapping(value = "/get/category")
    public List<Category> getCategories(){
        return catalogService.getCategories(jdbcTemplate);
    }

    @GetMapping(value = "/get/rifle")
    public List<Weapon>getRifles(){
        return catalogService.getRifles(jdbcTemplate);
    }

    @GetMapping(value = "/get/shotgun")
    public List<Weapon>getShotguns(){
        return catalogService.getShotguns(jdbcTemplate);
    }

    @GetMapping(value = "/get/airgun")
    public List<Weapon>getAirguns(){
        return catalogService.getAirguns(jdbcTemplate);
    }

    @GetMapping(value = "/get/ammo")
    public List<Ammo>getAmmo(){
        return catalogService.getAmmo(jdbcTemplate);
    }

    @GetMapping(value = "/get/target")
    public List<Target>getTarget(){
        return catalogService.getTargets(jdbcTemplate);
    }
}
