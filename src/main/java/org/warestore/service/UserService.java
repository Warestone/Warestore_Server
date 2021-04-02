package org.warestore.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.mapper.OrderMapper;
import org.warestore.mapper.UserMapper;
import org.warestore.model.EditPassword;
import org.warestore.model.Order;
import org.warestore.model.User;
import org.warestore.model.UserRegistration;
import org.warestore.service.enums.Attributes;
import org.warestore.service.enums.Types;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MailService mailService;

    public ResponseEntity<?> getUserByName(String username){
        Pattern pattern = Pattern.compile("[a-z0-9A-Z]{5,20}");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.find()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        log.info("Return user '"+username+"' by username.");
        List<User> users =  jdbcTemplate.query("select obj.id as id, param.attribute_id as atrrid, obj.name as username, usr.password as password, param.value from users usr, objects obj, parameters param \n" +
                        "where usr.object_id = obj.id and param.object_id = obj.id and obj.name = '"+username+"'  order by atrrid",
                new UserMapper());
        if (users.size()==0)return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(users.get(0), HttpStatus.OK);
    }

    public ResponseEntity<?> getUserByNameAndPassword(String username, String password){
        log.info("Return user '"+username+"' by username & password.");
        ResponseEntity<?> response = getUserByName(username);
        if (response.getStatusCode()==HttpStatus.OK){
            User user = (User) response.getBody();
            if (user!=null){
                if (passwordEncoder.matches(password, user.getPassword()))
                    return new ResponseEntity<>(user, HttpStatus.OK);
                else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public boolean updateInfo(UserRegistration userIn){
        ResponseEntity<?> response = getUserByName(userIn.getUsername());
        if (response.getStatusCode()!=HttpStatus.OK) return false;
        User user = (User) response.getBody();
        assert user != null;
        log.info("Update user info by username '"+user.getUsername()+"'.");
        //update parameters set value = 'Драгунов' where attribute_id = 20 and object_id = 76
        if (!userIn.getFirstName().equals(user.getFirstName()))
            jdbcTemplate.update(
                    "update parameters set value = '"+userIn.getFirstName()+
                            "' where attribute_id = "+(Attributes.FIRST_NAME.ordinal()+2)+
                            " and object_id = "+user.getId());
        if (!userIn.getLastName().equals(user.getLastName()))
            jdbcTemplate.update(
                    "update parameters set value = '"+userIn.getLastName()+
                            "' where attribute_id = "+(Attributes.LAST_NAME.ordinal()+2)+
                            " and object_id = "+user.getId());
        if (!userIn.getPatronymicName().equals(user.getPatronymicName()))
            jdbcTemplate.update(
                    "update parameters set value = '"+userIn.getPatronymicName()+
                            "' where attribute_id = "+(Attributes.PATRONYMIC_NAME.ordinal()+2)+
                            " and object_id = "+user.getId());
        if (!userIn.getPhoneNumber().equals(user.getPhoneNumber()))
            jdbcTemplate.update(
                    "update parameters set value = '"+userIn.getPhoneNumber()+
                            "' where attribute_id = "+Attributes.PHONE_NUMBER.ordinal()+
                            " and object_id = "+user.getId());
        if (!userIn.getAddress().equals(user.getAddress()))
            jdbcTemplate.update(
                    "update parameters set value = '"+userIn.getAddress()+
                            "' where attribute_id = "+Attributes.ADDRESS.ordinal()+
                            " and object_id = "+user.getId());
        if (!userIn.getEmail().equals(user.getEmail()))
            jdbcTemplate.update(
                    "update parameters set value = '"+userIn.getEmail()+
                            "' where attribute_id = "+Attributes.EMAIL.ordinal()+
                            " and object_id = "+user.getId());
        return true;
    }

    public boolean updatePassword(EditPassword editPassword, HttpServletRequest request){
        ResponseEntity<?> response = getUserByNameAndPassword(
                jwtProvider.getUsernameFromToken(jwtProvider.getTokenFromRequest(request)),
                editPassword.getCurrentPassword()
        );
        if (response.getStatusCode()!=HttpStatus.OK)
            return false;
        User user = (User) response.getBody();
        assert user != null;
        log.info("Update user password by username '"+user.getUsername()+"'.");
        jdbcTemplate.update(
                "update users set password = '"+passwordEncoder.encode(editPassword.getNewPassword())+
                        "' where object_id = "+user.getId());
        log.info("Sending message (new password) to user '"+user.getUsername()+"'.");
        mailService.sendMessage(user.getEmail(),"Новый пароль в WARESTORE",
                mailService.compileNewPasswordMessage(user));
        return true;
    }

    @Transactional
    public boolean saveUser(UserRegistration user){
        if (getUserByName(user.getUsername()).getStatusCode()==HttpStatus.OK) return false;
        log.info("Save user '"+user.getUsername()+"'.");
        jdbcTemplate.update("insert into objects (name, type_id) values " +
                "('"+user.getUsername()+"',"+Types.USER.ordinal()+")");

        String idUser = jdbcTemplate.queryForObject("select id from objects where name = '"+user.getUsername()+"'",String.class);

        jdbcTemplate.update("insert into users (object_id, password) values " +
                "("+idUser+", '"+passwordEncoder.encode(user.getPassword())+"')");

        jdbcTemplate.update("insert into parameters (object_id, attribute_id, value) values " +
                "("+idUser+","+Attributes.ROLE.ordinal()+",'ROLE_USER')," +
                "("+idUser+","+Attributes.EMAIL.ordinal()+",'"+user.getEmail()+"')," +
                "("+idUser+","+Attributes.PHONE_NUMBER.ordinal()+",'"+user.getPhoneNumber()+"')," +
                "("+idUser+","+(Attributes.FIRST_NAME.ordinal()+2)+",'"+user.getFirstName()+"')," +
                "("+idUser+","+(Attributes.LAST_NAME.ordinal()+2)+",'"+user.getLastName()+"')," +
                "("+idUser+","+(Attributes.PATRONYMIC_NAME.ordinal()+2)+",'"+user.getPatronymicName()+"'),"+
                "("+idUser+","+Attributes.ADDRESS.ordinal()+",'"+user.getAddress()+"')"
        );
        return true;
    }

    public ResponseEntity<?>getOrdersByUsername(HttpServletRequest request, int page){
        ResponseEntity<?> response = getUserByName(
                jwtProvider.getUsernameFromToken(
                jwtProvider.getTokenFromRequest(request)));
        if (response.getStatusCode()!=HttpStatus.OK)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        User user = (User) response.getBody();
        List<Order> ordersList = jdbcTemplate.query("select obj.id, obj.name, obj2.name as product_name, obj3.name as username, attr.name as type, param.value " +
                "from objects obj, objects obj2, objects obj3, attributes attr, parameters param, links ls, links ls2 " +
                "where ls2.object_id = obj.id and obj2.id = ls2.reference_obj_id and ls2.type_id = 3 " +
                "and ls.object_id = obj.id and param.object_id = obj.id and param.attribute_id = attr.id " +
                "and obj.type_id = 5 and obj3.id = ls.reference_obj_id and obj3.type_id=1 and ls.reference_obj_id = "+user.getId()+" order by id, type limit 20 offset "+page*20, new OrderMapper());

        return new ResponseEntity<>(ordersList, HttpStatus.OK);
    }
}
