package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "attribute_type")
public class AttributeTypeDTO {
    @Id
    private int attribute_id;

    //@Id
    private int type_id;
}
