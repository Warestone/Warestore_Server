package org.warestore.model;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class User {
    private int id;

    @Pattern(regexp = "[a-z0-9A-Z]{5,20}")
    private String username;
    private String password;
    private String role;
    /*private String email;
    private String phoneNumber;
    private String address;
    private ? items;*/
}
