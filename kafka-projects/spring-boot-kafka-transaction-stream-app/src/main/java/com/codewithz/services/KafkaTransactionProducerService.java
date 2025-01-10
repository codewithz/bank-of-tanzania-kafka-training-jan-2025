package com.codewithz.services;

import com.codewithz.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTransactionProducerService {

    @Value("${spring.kafka.topic.name:bot_tx_data}")
    private String topicName;

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Autowired
    public KafkaTransactionProducerService(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(Transaction transaction) {
        kafkaTemplate.send(topicName, transaction);
    }


}
