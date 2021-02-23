package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "attribute_type")
public class AttributeTypeDTO {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private int attribute_id;

    private int type_id;
}
