package org.warestore.model.object;

import lombok.Data;

@Data
public class Rifle {
    private int id;
    private String name;
    private String description;
    private int quantity;
    private double price;
}
