package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.model.User;
import org.warestore.model.UserAuthentication;
import org.warestore.model.UserRegistration;
import org.warestore.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping(value = "/server/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping(value = "/get/{username}")
    public User getUserByUsername(@PathVariable String username){
        return userService.getUserByName(username);
    }

    @PostMapping(value = "/register")
    public String registerUser(@RequestBody @Valid UserRegistration userRegistration){
        if (userService.getUserByName(userRegistration.getUsername())==null){
            User user = new User();
            //add all users parameters
            user.setUsername(userRegistration.getUsername());
            user.setPassword(userRegistration.getPassword());
            return user.toString();
        }
        else return null;
    }

    @PostMapping(value = "/auth")
    public String authUser(@RequestBody @Valid UserAuthentication userAuthentication){
        User user = userService.getUserByNameAndPassword(userAuthentication.getUsername(),
                userAuthentication.getPassword());
        if (user==null) return "user not found";
        return jwtProvider.generateToken(user.getUsername());
    }
}
