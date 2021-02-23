package org.warestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.warestore.dao.*;
import org.warestore.mapper.CategoryMapper;
import org.warestore.mapper.RifleMapper;
import org.warestore.model.dto.*;
import org.warestore.model.object.Category;
import org.warestore.model.object.Rifle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ServerService {
    private List<Category> categoriesList = new ArrayList<>();
    private List<Rifle> riflesList = new ArrayList<>();
    private final Logger logger = Logger.getLogger(ServerService.class.getName());
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

    private enum Types{

    }

    public List<Category> getCategories(JdbcTemplate jdbcTemplate){
        logger.info("Return categories.");
        if (categoriesList.size()!=0) return categoriesList;
        return categoriesList = jdbcTemplate.query(
                "select id, name from objects where type_id = 6", new CategoryMapper());
    }

    public List<Rifle> getRifles(JdbcTemplate jdbcTemplate){
        logger.info("Return rifles.");
        return riflesList = jdbcTemplate.query(
                "", new RifleMapper());
    }

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
