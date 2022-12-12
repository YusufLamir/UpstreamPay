package com.exercice.upstreampay.repository;

import com.exercice.upstreampay.entities.OrderLine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the OrderLine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderLineRepository extends ReactiveMongoRepository<OrderLine, String> {
    Flux<OrderLine> findAllBy(Pageable pageable);
}
