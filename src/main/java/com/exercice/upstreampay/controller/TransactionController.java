package com.exercice.upstreampay.controller;

import com.exercice.upstreampay.controller.error.BadRequestAlertException;
import com.exercice.upstreampay.controller.utils.HeaderUtil;
import com.exercice.upstreampay.controller.utils.PaginationUtil;
import com.exercice.upstreampay.controller.utils.ResponseUtil;
import com.exercice.upstreampay.controller.validator.TransactionValidator;
import com.exercice.upstreampay.entities.Transaction;
import com.exercice.upstreampay.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private static final String ENTITY_NAME = "transaction";
    private final TransactionService transactionService;
    private final TransactionValidator transactionValidator;

    public TransactionController(TransactionService transactionService, TransactionValidator transactionValidator) {
        this.transactionService = transactionService;
        this.transactionValidator = transactionValidator;
    }

    private final Logger log = LoggerFactory.getLogger(TransactionController.class);

    /**
     * {@code POST  /transactions} : Create a new transaction.
     *
     * @param transaction the transaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transaction, or with status {@code 400 (Bad Request)} if the transaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transactions")
    @Operation(description = "create new transaction.")
    public Mono<ResponseEntity<Transaction>> createTransaction(@Valid @RequestBody Transaction transaction) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transaction);
        if (transaction.getId() != null) {
            throw new BadRequestAlertException("A new transaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        transactionValidator.validate(transaction);
        return transactionService
                .save(transaction)
                .map(result -> {
                    try {
                        return ResponseEntity
                                .created(new URI("/api/transactions/" + result.getId()))
                                .headers(HeaderUtil.createEntityCreationAlert("", true, ENTITY_NAME, result.getId()))
                                .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * {@code PUT  /transactions/:id} : Updates an existing transaction.
     *
     * @param id          the id of the transaction to save.
     * @param transaction the transaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transaction,
     * or with status {@code 400 (Bad Request)} if the transaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transactions/{id}")
    @Operation(description = "edit the given transaction by id.")
    public Mono<ResponseEntity<Transaction>> updateTransaction(
            @PathVariable(value = "id", required = false) final String id,
            @Valid @RequestBody Transaction transaction
    ) throws URISyntaxException {
        log.debug("REST request to update Transaction : {}, {}", id, transaction);
        if (transaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return transactionService
                .existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return transactionService
                            .update(transaction)
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                            .map(result ->
                                    ResponseEntity
                                            .ok()
                                            .headers(HeaderUtil.createEntityUpdateAlert("", true, ENTITY_NAME, result.getId()))
                                            .body(result)
                            );
                });
    }

    /**
     * {@code GET  /transactions} : get all the transactions.
     *
     * @param pageable the pagination information.
     * @param request  a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactions in body.
     */
    @GetMapping("/transactions")
    @Operation(description = "get all transactions.")
    public Mono<ResponseEntity<List<Transaction>>> getAllTransactions(
            @org.springdoc.api.annotations.ParameterObject Pageable pageable,
            ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Transactions");
        return transactionService
                .countAll()
                .zipWith(transactionService.findAll(pageable).collectList())
                .map(countWithEntities ->
                        ResponseEntity
                                .ok()
                                .headers(
                                        PaginationUtil.generatePaginationHttpHeaders(
                                                UriComponentsBuilder.fromHttpRequest(request),
                                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                                        )
                                )
                                .body(countWithEntities.getT2())
                );
    }

    /**
     * {@code GET  /transactions/:id} : get the "id" transaction.
     *
     * @param id the id of the transaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transactions/{id}")
    @Operation(description = "get transaction by id.")
    public Mono<ResponseEntity<Transaction>> getTransaction(@PathVariable String id) {
        log.debug("REST request to get Transaction : {}", id);
        Mono<Transaction> transaction = transactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transaction);
    }

}
