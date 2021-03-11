package org.warestore.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String email;
    private String phoneNumber;
    private String address;
}
