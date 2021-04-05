package org.warestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;


import javax.validation.constraints.*;

@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    private int id;

    @NotEmpty
    @Pattern(regexp = "[a-z][a-z0-9]{3,20}")
    private String username;

    @NotEmpty
    @Size(min = 4, max = 12)
    private String password;

    @NotEmpty
    @Pattern(regexp = "[А-Я][а-я]{3,20}")
    private String firstName;

    @NotEmpty
    @Pattern(regexp = "[А-Я][а-я]{3,20}")
    private String lastName;

    @NotEmpty
    @Pattern(regexp = "[А-Я][а-я]{5,20}")
    private String patronymicName;

    private String role;

    @NotEmpty
    @Pattern(regexp = "[a-z0-9\\\\.]{3,200}@[a-z0-9]{3,20}.?[a-z]{2,20}?")
    private String email;

    @NotEmpty
    @Pattern(regexp = "\\+[0-9]\\([0-9]{3}\\)-[0-9]{3}\\-[0-9]{2}\\-[0-9]{2}")
    private String phoneNumber;

    @NotEmpty
    @Size(min = 10, max = 50)
    private String address;

    @JsonIgnore
    public String getFIO(){ return lastName+" "+firstName+" "+patronymicName; }
}
