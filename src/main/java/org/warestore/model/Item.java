package org.warestore.model;

import lombok.Data;

@Data
public class Item {
    int id;
    String name;
    int quantity;
    double price;
}
