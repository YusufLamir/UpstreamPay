package com.exercice.upstreampay.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLine {

    private String productName;
    private int quantity;
    private double price;
}
