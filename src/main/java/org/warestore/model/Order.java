package org.warestore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends Item{
    private String date;
    private String status;
    private String nameOrder;
    private String username;
}
