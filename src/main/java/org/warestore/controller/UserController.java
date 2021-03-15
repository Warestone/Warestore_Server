package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.model.User;
import org.warestore.model.UserAuthentication;
import org.warestore.model.UserRegistration;
import org.warestore.service.UserService;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/server/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping(value = "/get/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        User user =  userService.getUserByName(username);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public List<String> registerUser(@RequestBody @Valid UserRegistration userRegistration){
        UserRegistration user = userService.saveUser(userRegistration);
        if (user == null) return null;
        else return authUser(new UserAuthentication(userRegistration.getUsername(),
                userRegistration.getPassword()));
    }

    @PostMapping(value = "/auth")
    public List<String> authUser(@RequestBody @Valid UserAuthentication userAuthentication){
        User user = userService.getUserByNameAndPassword(userAuthentication.getUsername(),
                userAuthentication.getPassword());
        if (user==null) return null;
        else return Collections.singletonList(jwtProvider.generateToken(user.getUsername()));
    }
}
