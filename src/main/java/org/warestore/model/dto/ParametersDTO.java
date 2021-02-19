package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "parameters")
public class ParametersDTO {
    @Id
    private int object_id;

    //@Id
    private int attribute_id;

    //@Id
    private String value;
}
