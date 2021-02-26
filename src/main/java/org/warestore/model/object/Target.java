package org.warestore.model.object;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Target extends Product{
    private String size;
}
