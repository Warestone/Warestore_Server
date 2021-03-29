package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.model.Token;
import org.warestore.model.UserAuthentication;
import org.warestore.model.UserRegistration;
import org.warestore.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/server/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping(value = "/get")
    public ResponseEntity<?> getUserByUsername(HttpServletRequest request){
        return userService.getUserByName(
                jwtProvider.getUsernameFromToken(
                        jwtProvider.getTokenFromRequest(request)));
    }

    @GetMapping("/get/order_page/{page}")
    public ResponseEntity<?> getUserOrders(HttpServletRequest request, @PathVariable int page){
        return userService.getOrdersByUsername(request, page);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistration userRegistration){
        boolean register = userService.saveUser(userRegistration);
        if (!register) new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        return authUser(new UserAuthentication(userRegistration.getUsername(),
                userRegistration.getPassword()));
    }

    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authUser(@RequestBody @Valid UserAuthentication userAuthentication){
        ResponseEntity<?> response = userService.getUserByNameAndPassword(userAuthentication.getUsername(),
                userAuthentication.getPassword());
        if (response.getStatusCode()!=HttpStatus.OK) return response;
        else return new ResponseEntity<>(new Token(jwtProvider.generateToken(userAuthentication.getUsername())),
                HttpStatus.OK);
    }
}
