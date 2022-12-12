package com.exercice.upstreampay.service.impl;

import com.exercice.upstreampay.controller.error.BadRequestAlertException;
import com.exercice.upstreampay.entities.Transaction;
import com.exercice.upstreampay.entities.enumerations.Status;
import com.exercice.upstreampay.repository.TransactionRepository;
import com.exercice.upstreampay.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        log.debug("Request to save Transaction : {}", transaction);
        if (!transaction.getStatus().equals(Status.NEW)) {
            throw new BadRequestAlertException("A new transaction must be with NEW status", "Transaction", "error.transaction.status");
        }
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> update(Transaction givenTransaction) {
        log.debug("Request to update Transaction : {}", givenTransaction);

        return transactionRepository.findById(givenTransaction.getId()).flatMap(transaction -> {
            String givenStatus = givenTransaction.getStatus().toString();
            String status = Objects.requireNonNull(transaction).getStatus().toString();

            boolean checkStatus = verifyTransactionStatus(givenTransaction.getStatus(), transaction);
            if (!checkStatus) {
                throw new BadRequestAlertException("The status" + status + " cannot be modified as" + givenStatus, "Transaction", "error.transaction.update");
            }
            boolean checkOrder = verifyTransactionOrder(transaction, givenTransaction);
            if (!checkOrder) {
                throw new BadRequestAlertException("Orders must be immutable", "Transaction", "error.transaction.update");
            }
            return transactionRepository.save(givenTransaction);
        });
    }

    @Override
    public Flux<Transaction> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAllBy(pageable);
    }

    @Override
    public Mono<Transaction> findOne(String id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return transactionRepository.existsById(id);
    }

    private boolean verifyTransactionStatus(Status status, Transaction transaction) {
        if (Objects.requireNonNull(transaction).getStatus().equals(Status.CAPTURED) && !status.equals(Status.CAPTURED)) {
            return false;
        }
        return !Objects.requireNonNull(transaction).getStatus().equals(Status.NEW) || !status.equals(Status.CAPTURED);
    }

    public boolean verifyTransactionOrder(Transaction savedTransaction, Transaction transaction) {
        return Objects.requireNonNull(savedTransaction).getOrders().size() == transaction.getOrders().size() &&
                Objects.requireNonNull(savedTransaction).getOrders().stream().filter(order1 -> !transaction.getOrders().contains(order1)).count() <= 0;
    }

    public Mono<Long> countAll() {
        return transactionRepository.count();
    }
}
