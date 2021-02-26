package org.warestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warestore.dao.*;
import org.warestore.model.dto.*;
import java.util.logging.Logger;

@Service
public class PostService {
    private final Logger logger = Logger.getLogger(PostService.class.getName());

    @Autowired
    private AttributesDAO attributesDAO;
    @Autowired
    private AttributeTypeDAO attributeTypeDAO;
    @Autowired
    private LinksDAO linksDAO;
    @Autowired
    private ObjectsDAO objectsDAO;
    @Autowired
    private ParametersDAO parametersDAO;
    @Autowired
    private TypesDAO typesDAO;
    @Autowired
    private UsersDAO usersDAO;



    public String addObject(ObjectsDTO objectDTO){
        objectsDAO.save(objectDTO);
        logger.info("Add new object with name '"+objectDTO.getName()+"'.");
        return "200";
    }

    public String addParameter(ParametersDTO parameterDTO){
        parametersDAO.save(parameterDTO);
        logger.info("Add new parameter with value '"+parameterDTO.getValue()+"'.");
        return "200";
    }

    public String addLink(LinksDTO linkDTO){
        linksDAO.save(linkDTO);
        logger.info("Add new link with reference object '"+linkDTO.getReference_obj()+"'.");
        return "200";
    }

    public String addAttribute(AttributesDTO attributeDTO){
        attributesDAO.save(attributeDTO);
        logger.info("Add new attribute with name '"+attributeDTO.getName()+"'.");
        return "200";
    }

    public String addUser(UsersDTO userDTO){
        usersDAO.save(userDTO);
        logger.info("Add new user with id '"+userDTO.getId()+"'.");
        return "200";
    }

    public String addType(TypesDTO typeDTO){
        typesDAO.save(typeDTO);
        logger.info("Add new type with name '"+typeDTO.getName()+"'.");
        return "200";
    }

    public String addAttributeType(AttributeTypeDTO attributeTypeDTO){
        attributeTypeDAO.save(attributeTypeDTO);
        logger.info("Add new attribute type with attribute id '"+attributeTypeDTO.getAttribute_id()+"'.");
        return "200";
    }
}
