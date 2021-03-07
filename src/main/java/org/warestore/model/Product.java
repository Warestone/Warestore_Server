package org.warestore.model;

import lombok.Data;

@Data
public class Product {
    private int id;
    private String name;
    private String description;
    private int quantity;
    private double price;
}
