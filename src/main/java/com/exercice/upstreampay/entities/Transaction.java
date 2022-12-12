package com.exercice.upstreampay.entities;

import com.exercice.upstreampay.entities.enumerations.PaymentType;
import com.exercice.upstreampay.entities.enumerations.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {

    @Id
    private String id;
    private double price;
    private PaymentType type;
    private Status status;
    private List<OrderLine> orders;


}
