package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "parameters")
public class ParametersDTO {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private int object_id;

    private int attribute_id;

    private String value;
}
