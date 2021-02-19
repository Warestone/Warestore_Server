package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
public class UsersDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //@Id
    private int object_id;

    private String password;
}
