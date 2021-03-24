package org.warestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Item {
    int id;
    String name;
    int quantity;
    double price;

    @JsonIgnore
    int qtyInWarehouse;

    @JsonIgnore
    public double getTotalPrice(){
        return quantity*price;
    }
}
