package org.warestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.warestore.configuration.jwt.JwtProvider;
import org.warestore.model.EditPassword;
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
        try {
            return userService.getUserByName(
                    jwtProvider.getUsernameFromToken(
                            jwtProvider.getTokenFromRequest(request)));
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get/order_page/{page}")
    public ResponseEntity<?> getUserOrders(HttpServletRequest request, @PathVariable int page){
        try{
            return userService.getOrdersByUsername(request, page);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistration userRegistration){
        try {
            ResponseEntity<?> register = userService.saveUser(userRegistration);
            if (register.getStatusCode()!=HttpStatus.OK) return register;

            else return authUser(new UserAuthentication(userRegistration.getUsername(),
                    userRegistration.getPassword()));
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authUser(@RequestBody @Valid UserAuthentication userAuthentication){
        try{
            ResponseEntity<?> response = userService.getUserByNameAndPassword(userAuthentication.getUsername(),
                    userAuthentication.getPassword());
            if (response.getStatusCode()!=HttpStatus.OK) return response;
            else return new ResponseEntity<>(new Token(jwtProvider.generateToken(userAuthentication.getUsername())),
                    HttpStatus.OK);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/post/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserInfo(@RequestBody @Valid UserRegistration user){
        try{
            return userService.updateInfo(user);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/post/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserPassword(@RequestBody @Valid EditPassword editPasswordIn, HttpServletRequest request){
        try{
            boolean editPassword = userService.updatePassword(editPasswordIn, request);
            if (!editPassword) return new ResponseEntity<>(HttpStatus.CONFLICT);
            else return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
