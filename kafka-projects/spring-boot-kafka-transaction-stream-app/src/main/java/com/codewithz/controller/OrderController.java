package com.codewithz.controller;

import com.codewithz.model.Order;
import com.codewithz.model.Transaction;
import com.codewithz.services.KafkaOrderProducerService;
import com.codewithz.services.KafkaTransactionProducerService;
import com.codewithz.streams.OrderStreamProcessor;
import com.codewithz.streams.TransactionStreamProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    KafkaOrderProducerService kafkaOrderProducerService;

    @Autowired
    private OrderStreamProcessor orderStreamProcessor;

    @PostMapping
    public ResponseEntity<Order> processTransaction(@RequestBody Order order) {
        try {
            // Send raw transaction to Kafka topic

            kafkaOrderProducerService.sendOrder(order);
            // Wait for validation and transformation
            Order validatedOrder=orderStreamProcessor.getValidatedOrder();
            return ResponseEntity.ok(validatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

