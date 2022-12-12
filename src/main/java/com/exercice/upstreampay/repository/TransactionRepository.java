package com.exercice.upstreampay.repository;

import com.exercice.upstreampay.entities.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findAllBy(Pageable pageable);
}
