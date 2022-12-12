package com.exercice.upstreampay.service;

import com.exercice.upstreampay.entities.Transaction;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    /**
     * save a Transaction
     *
     * @param transaction
     * @return Mono<Transaction>
     */
    Mono<Transaction> save(Transaction transaction);

    /**
     * Updates a transaction.
     *
     * @param transaction the entity to update.
     * @return the persisted entity.
     */
    Mono<Transaction> update(Transaction transaction);

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Transaction> findAll(Pageable pageable);

    /**
     * Get the "id" transaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Transaction> findOne(String id);

    /**
     * check if transaction exists by id
     *
     * @param id
     * @return the status
     */
    Mono<Boolean> existsById(String id);

    /**
     * Returns the number of transactions available.
     *
     * @return the number of entities in the database.
     */
    Mono<Long> countAll();
}
