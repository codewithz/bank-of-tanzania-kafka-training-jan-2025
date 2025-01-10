package com.codewithz.controller;

import com.codewithz.model.Transaction;
import com.codewithz.services.KafkaTransactionProducerService;
import com.codewithz.services.TransactionValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    TransactionValidationService transactionValidationService;
    KafkaTransactionProducerService kafkaTransactionProducerService;

    public TransactionController(TransactionValidationService transactionValidationService, KafkaTransactionProducerService kafkaTransactionProducerService) {
        this.transactionValidationService = transactionValidationService;
        this.kafkaTransactionProducerService = kafkaTransactionProducerService;
    }
    @PostMapping
    public ResponseEntity<String> proceessTransaction(@RequestBody Transaction transaction) {
//        Validate the Transaction
        Transaction validatedTransaction=transactionValidationService.validateAndTransform(transaction);

        kafkaTransactionProducerService.sendTransaction(validatedTransaction);
        System.out.println("Transaction successfully validated");
        return ResponseEntity.ok("Transaction successfully validated");
    }
}
