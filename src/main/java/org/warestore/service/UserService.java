package org.warestore.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.model.EditPassword;
import org.warestore.model.Order;
import org.warestore.model.User;
import org.warestore.model.UserRegistration;
import org.warestore.repository.UserRepository;
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
    @Autowired
    private Cluster cluster;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> getUserByName(String username){
        Pattern pattern = Pattern.compile("[a-z0-9A-Z]{4,20}");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.find()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        log.info("Return user '"+username+"' by username.");
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.User' and username='"+username+"'");
        List<User> user = queryResult.rowsAs(User.class);
        if (user.size()==0)return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(user.get(0), HttpStatus.OK);
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
        if (!userIn.getFirstName().equals(user.getFirstName())) {
            jdbcTemplate.update(
                    "update parameters set value = '" + userIn.getFirstName() +
                            "' where attribute_id = " + (Attributes.FIRST_NAME.ordinal() + 2) +
                            " and object_id = " + user.getId());
            user.setFirstName(userIn.getFirstName());
        }
        if (!userIn.getLastName().equals(user.getLastName())) {
            jdbcTemplate.update(
                    "update parameters set value = '" + userIn.getLastName() +
                            "' where attribute_id = " + (Attributes.LAST_NAME.ordinal() + 2) +
                            " and object_id = " + user.getId());
            user.setLastName(userIn.getLastName());
        }
        if (!userIn.getPatronymicName().equals(user.getPatronymicName())) {
            jdbcTemplate.update(
                    "update parameters set value = '" + userIn.getPatronymicName() +
                            "' where attribute_id = " + (Attributes.PATRONYMIC_NAME.ordinal() + 2) +
                            " and object_id = " + user.getId());
            user.setPatronymicName(userIn.getPatronymicName());
        }
        if (!userIn.getPhoneNumber().equals(user.getPhoneNumber())) {
            jdbcTemplate.update(
                    "update parameters set value = '" + userIn.getPhoneNumber() +
                            "' where attribute_id = " + Attributes.PHONE_NUMBER.ordinal() +
                            " and object_id = " + user.getId());
            user.setPhoneNumber(userIn.getPhoneNumber());
        }
        if (!userIn.getAddress().equals(user.getAddress())) {
            jdbcTemplate.update(
                    "update parameters set value = '" + userIn.getAddress() +
                            "' where attribute_id = " + Attributes.ADDRESS.ordinal() +
                            " and object_id = " + user.getId());
            user.setAddress(userIn.getAddress());
        }
        if (!userIn.getEmail().equals(user.getEmail())) {
            jdbcTemplate.update(
                    "update parameters set value = '" + userIn.getEmail() +
                            "' where attribute_id = " + Attributes.EMAIL.ordinal() +
                            " and object_id = " + user.getId());
            user.setEmail(userIn.getEmail());
        }
        //update user in couchbase
        userRepository.save(user);
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
        String password = passwordEncoder.encode(editPassword.getNewPassword());
        jdbcTemplate.update(
                "update users set password = '"+password+
                        "' where object_id = "+user.getId());

        //update password in couchbase
        user.setPassword(password);
        userRepository.save(user);
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
        String password = passwordEncoder.encode(user.getPassword());

        jdbcTemplate.update("insert into users (object_id, password) values " +
                "("+idUser+", '"+password+"')");

        jdbcTemplate.update("insert into parameters (object_id, attribute_id, value) values " +
                "("+idUser+","+Attributes.ROLE.ordinal()+",'ROLE_USER')," +
                "("+idUser+","+Attributes.EMAIL.ordinal()+",'"+user.getEmail()+"')," +
                "("+idUser+","+Attributes.PHONE_NUMBER.ordinal()+",'"+user.getPhoneNumber()+"')," +
                "("+idUser+","+(Attributes.FIRST_NAME.ordinal()+2)+",'"+user.getFirstName()+"')," +
                "("+idUser+","+(Attributes.LAST_NAME.ordinal()+2)+",'"+user.getLastName()+"')," +
                "("+idUser+","+(Attributes.PATRONYMIC_NAME.ordinal()+2)+",'"+user.getPatronymicName()+"'),"+
                "("+idUser+","+Attributes.ADDRESS.ordinal()+",'"+user.getAddress()+"')"
        );

        //save user in couchbase
        User newUser = new User();
        newUser.setId(Integer.parseInt(idUser));
        newUser.setUsername(user.getUsername());
        newUser.setPassword(password);
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPatronymicName(user.getPatronymicName());
        newUser.setRole("ROLE_USER");
        newUser.setEmail(user.getEmail());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setAddress(user.getAddress());
        userRepository.save(newUser);
        return true;
    }

    public ResponseEntity<?>getOrdersByUsername(HttpServletRequest request, int page){
        ResponseEntity<?> response = getUserByName(
                jwtProvider.getUsernameFromToken(
                jwtProvider.getTokenFromRequest(request)));
        if (response.getStatusCode()!=HttpStatus.OK)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        User user = (User) response.getBody();
        QueryResult queryResult = cluster.query(
                "SELECT d.*, meta(d).id FROM warestore AS d where _class='org.warestore.model.Order' and username='"+user.getUsername()+"' limit 5 offset "+page*5);
        List<Order> orders = queryResult.rowsAs(Order.class);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
