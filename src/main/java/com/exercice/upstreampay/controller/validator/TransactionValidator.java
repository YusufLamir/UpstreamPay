package com.exercice.upstreampay.controller.validator;

import com.exercice.upstreampay.controller.error.BadRequestAlertException;
import com.exercice.upstreampay.entities.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class TransactionValidator implements Validator<Transaction> {

    @Override
    public void validate(Transaction transaction) {
        if (transaction.getStatus() == null || StringUtils.isBlank(transaction.getStatus().toString())) {
            throw new BadRequestAlertException("The status must be not empty", "Transaction", "error.status");
        }
        if (transaction.getType() == null || StringUtils.isBlank(transaction.getType().toString())) {
            throw new BadRequestAlertException("The type must be not empty", "Transaction", "error.type");
        }
        if (transaction.getPrice() <= 0.0) {
            throw new BadRequestAlertException("The price must be positive", "Transaction", "error.price");
        }
        if (CollectionUtils.isEmpty(transaction.getOrders())) {
            throw new BadRequestAlertException("The orders must be not empty", "Transaction", "error.orders");
        }
    }
}
