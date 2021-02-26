package org.warestore.controller;

import org.springframework.web.bind.annotation.*;
import org.warestore.model.dto.*;
import org.warestore.service.PostService;

@RestController
@RequestMapping(value = "/server/post")
public class PostController {

    private final PostService postService = new PostService();

    @PostMapping(value = "/object")
    public String addObject(@RequestBody ObjectsDTO objectDTO){
        return postService.addObject(objectDTO);
    }

    @PostMapping(value = "/parameter")
    public String addParameter(@RequestBody ParametersDTO parameterDTO){ return postService.addParameter(parameterDTO); }

    @PostMapping(value = "/link")
    public String addLink(@RequestBody LinksDTO linksDTO){
        return postService.addLink(linksDTO);
    }

    @PostMapping(value = "/attribute")
    public String addAttribute(@RequestBody AttributesDTO attributesDTO){ return postService.addAttribute(attributesDTO); }

    @PostMapping(value = "/user")
    public String addUser(@RequestBody UsersDTO usersDTO){
        return postService.addUser(usersDTO);
    }

    @PostMapping(value = "/attribute_type")
    public String addAttributeType(@RequestBody AttributeTypeDTO attributeTypeDTO){ return postService.addAttributeType(attributeTypeDTO); }

    @PostMapping(value = "/type")
    public String addType(@RequestBody TypesDTO typesDTO){
        return postService.addType(typesDTO);
    }
}
