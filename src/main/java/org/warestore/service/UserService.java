package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.warestore.mapper.UserMapper;
import org.warestore.model.User;
import org.warestore.service.enums.Attributes;
import org.warestore.service.enums.Types;
import java.util.List;

@Log
@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByName(String username){
        log.info("Return user "+username+" by username.");
        List<User> users = jdbcTemplate.query("select usr.id as id, obj.name as username, usr.password as password, param.value from users usr, objects obj, parameters param \n" +
                "where usr.object_id = obj.id and param.object_id = obj.id and obj.name = '"+username+"'",
                new UserMapper());
        if (users.size()==0)return null;
        else return users.get(0);
    }

    public User getUserByNameAndPassword(String username, String password){
        log.info("Return user "+username+" by username & password.");
        User user = getUserByName(username);
        if (user!=null){
            if (passwordEncoder.matches(password, user.getPassword()))
                return user;
        }
        return null;
    }

    public User saveUser(User user){
        if (getUserByName(user.getUsername())!=null) return null;
        log.info("Save user "+user.getUsername()+".");
        jdbcTemplate.update("insert into objects (name, type_id) values " +
                "('"+user.getUsername()+"',"+Types.USER.ordinal()+")");

        jdbcTemplate.update("insert into users (object_id, password) values (" +
                "(select id from objects where name = '"+user.getUsername()+"')," +
                "('"+passwordEncoder.encode(user.getPassword())+"'))");

        //add all parameters in future
        jdbcTemplate.update("insert into parameters (object_id, attribute_id, value) values (" +
                "(select id from objects where name = '"+user.getUsername()+"')," +
                 Attributes.ROLE.ordinal() +",'ROLE_USER')");
        return user;
    }
}
