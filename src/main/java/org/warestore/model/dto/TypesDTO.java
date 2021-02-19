package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "types")
public class TypesDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //@Id
    private String name;
}
