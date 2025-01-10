package com.codewithz.streams;

import com.codewithz.model.Order;
import com.codewithz.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TransactionStreamProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LinkedBlockingQueue<Transaction> processedQueue = new LinkedBlockingQueue<>();
    private final Map kafkaStreamsConfiguration;

    public TransactionStreamProcessor(Map kafkaStreamsConfiguration) {
        this.kafkaStreamsConfiguration = kafkaStreamsConfiguration;
    }

    @PostConstruct
    public void processStream() {
        StreamsBuilder builder = new StreamsBuilder();

        // Define input topic
        KStream<String, String> stream = builder.stream("bot_tx_data");

        // Validate and transform
        stream.mapValues(this::validateAndTransformTransaction)
                .filter((key, value) -> value != null)
                .foreach((key, value) -> {
                    try {
                        processedQueue.put(objectMapper.readValue(value, Transaction.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });



        // Kafka Streams configuration and start
        new KafkaStreams(builder.build(), new StreamsConfig(kafkaStreamsConfiguration)).start();
    }

    private String validateAndTransformTransaction(String transactionJson) {
        try {
            Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);

            // Validation
            if (transaction.getAmount() <= 100) {
                throw new IllegalArgumentException("Transaction amount must be greater than 100 USD");
            }

            // Transformation
            String txType;
            switch (transaction.getTxTypeCode()) {
                case "1":
                    txType = "Savings";
                    break;
                case "2":
                    txType = "Credit";
                    break;
                case "3":
                    txType = "Checking";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid transaction type code");
            }
            transaction.setTxTypeCode(txType);

            return objectMapper.writeValueAsString(transaction);

        } catch (Exception e) {
          //  e.printStackTrace();

        }
        return null;
    }

    public Transaction getValidatedTransaction() {
        try {
            return processedQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for transaction", e);
        }
    }




}
