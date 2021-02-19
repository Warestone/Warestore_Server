package org.warestore.model.dto;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "links")
public class LinksDTO {
    @Id
    private int attribute_id;

    //@Id
    private int object_id;

    private int reference_obj;
}
