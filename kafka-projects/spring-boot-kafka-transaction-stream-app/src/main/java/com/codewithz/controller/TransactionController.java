package com.codewithz.controller;

import com.codewithz.model.Transaction;

import com.codewithz.services.KafkaTransactionProducerService;
import com.codewithz.streams.TransactionStreamProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    KafkaTransactionProducerService kafkaTransactionProducerService;

    @Autowired
    private TransactionStreamProcessor transactionStreamProcessor;

    @PostMapping
    public ResponseEntity<Transaction> processTransaction(@RequestBody Transaction transaction) {
        try {
            // Send raw transaction to Kafka topic

            kafkaTransactionProducerService.sendTransaction(transaction);
            // Wait for validation and transformation
            Transaction validatedTransaction = transactionStreamProcessor.getValidatedTransaction();
            if(validatedTransaction ==null){
                return new ResponseEntity<>(HttpStatusCode.valueOf(404));
            }
            return ResponseEntity.ok(validatedTransaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

