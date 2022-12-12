package com.exercice.upstreampay;

import com.exercice.upstreampay.entities.OrderLine;
import com.exercice.upstreampay.entities.Transaction;
import com.exercice.upstreampay.entities.enumerations.PaymentType;
import com.exercice.upstreampay.entities.enumerations.Status;
import com.exercice.upstreampay.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringBootApplication
public class UpstreamPayApplication implements CommandLineRunner {
    @Autowired
    private TransactionService transactionService;

    private final Logger log = LoggerFactory.getLogger(UpstreamPayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UpstreamPayApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Transaction transaction1 = new Transaction(null, 54.80, PaymentType.BANK_CARD, Status.NEW, List.of(
                new OrderLine("gants de ski", 4, 10), new OrderLine("bonnet en laine", 1, 14.80)
        ));
        transactionService.save(transaction1).subscribe(transaction -> {
            transaction.setStatus(Status.AUTHORIZED);
            transactionService.update(transaction).subscribe(trans -> {
                trans.setStatus(Status.CAPTURED);
                transactionService.update(trans).subscribe();
            });
        });

        Transaction transaction2 = new Transaction(null, 208, PaymentType.PAYPAL, Status.NEW, List.of(
                new OrderLine("Vélo", 1, 208)));
        transactionService.save(transaction2).subscribe();

        transactionService.findAll(PageRequest.of(0, 10)).subscribe(transaction -> {
            System.out.println(transaction.getOrders());
        });
    }
}
