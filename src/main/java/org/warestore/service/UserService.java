package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warestore.mapper.UserMapper;
import org.warestore.model.User;
import org.warestore.model.UserRegistration;
import org.warestore.service.enums.Attributes;
import org.warestore.service.enums.Types;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> getUserByName(String username){
        Pattern pattern = Pattern.compile("[a-z0-9A-Z]{5,20}");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.find()) return null;
        log.info("Return user "+username+" by username.");
        List<User> users =  jdbcTemplate.query("select usr.id as id, obj.name as username, usr.password as password, param.value from users usr, objects obj, parameters param \n" +
                "where usr.object_id = obj.id and param.object_id = obj.id and obj.name = '"+username+"'",
                new UserMapper());
        if (users.size()==0)return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(users.get(0), HttpStatus.OK);
    }

    public ResponseEntity<?> getUserByNameAndPassword(String username, String password){
        log.info("Return user "+username+" by username & password.");
        ResponseEntity<?> response = getUserByName(username);
        if (response.getStatusCode()==HttpStatus.OK){
            User user = (User) response.getBody();
            if (user!=null){
                if (passwordEncoder.matches(password, user.getPassword()))
                    return new ResponseEntity<>(HttpStatus.OK);
                else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //need test, but sure
    @Transactional
    public boolean saveUser(UserRegistration user){
        if (getUserByName(user.getUsername()).getStatusCode()==HttpStatus.OK) return false;
        log.info("Save user "+user.getUsername()+".");
        jdbcTemplate.update("insert into objects (name, type_id) values " +
                "('"+user.getUsername()+"',"+Types.USER.ordinal()+")");

        String idUser = jdbcTemplate.queryForObject("select id from objects where name = '"+user.getUsername()+"'",String.class);

        jdbcTemplate.update("insert into users (object_id, password) values " +
                "("+idUser+", '"+passwordEncoder.encode(user.getPassword())+"')");

        jdbcTemplate.update("insert into parameters (object_id, attribute_id, value) values " +
                "("+idUser+","+Attributes.ROLE.ordinal()+",'ROLE_USER')," +
                "("+idUser+","+Attributes.EMAIL.ordinal()+",'"+user.getEmail()+"')," +
                "("+idUser+","+Attributes.PHONE_NUMBER.ordinal()+",'"+user.getPhoneNumber()+"')," +
                "("+idUser+","+Attributes.ADDRESS.ordinal()+",'"+user.getAddress()+"')");

        return true;
    }
}
