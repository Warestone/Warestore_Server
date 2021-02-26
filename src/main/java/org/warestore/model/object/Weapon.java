package org.warestore.model.object;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Weapon extends Product{
    private String caliber;
}
