package org.warestore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Ammo extends Weapon{
    private int rounds;
}
