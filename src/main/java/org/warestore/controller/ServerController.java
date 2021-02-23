package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.warestore.model.dto.*;
import org.warestore.model.object.Category;
import org.warestore.service.ServerService;
import java.util.List;

@RestController
@RequestMapping(value = "/server")
public class ServerController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final ServerService serverService = new ServerService();

    @GetMapping(value = "/get/category")
    public List<Category>getCategories(){
        return serverService.getCategories(jdbcTemplate);
    }

    @PostMapping(value = "/post/object")
    public String addObject(@RequestBody ObjectsDTO objectDTO){
        return serverService.addObject(objectDTO);
    }

    @PostMapping(value = "/post/parameter")
    public String addParameter(@RequestBody ParametersDTO parameterDTO){
        return serverService.addParameter(parameterDTO);
    }

    @PostMapping(value = "/post/link")
    public String addLink(@RequestBody LinksDTO linksDTO){
        return serverService.addLink(linksDTO);
    }

    @PostMapping(value = "/post/attribute")
    public String addAttribute(@RequestBody AttributesDTO attributesDTO){
        return serverService.addAttribute(attributesDTO);
    }

    @PostMapping(value = "/post/user")
    public String addUser(@RequestBody UsersDTO usersDTO){
        return serverService.addUser(usersDTO);
    }

    @PostMapping(value = "/post/attribute_type")
    public String addAttributeType(@RequestBody AttributeTypeDTO attributeTypeDTO){
        return serverService.addAttributeType(attributeTypeDTO);
    }

    @PostMapping(value = "/post/type")
    public String addType(@RequestBody TypesDTO typesDTO){
        return serverService.addType(typesDTO);
    }
}
