package com.codewithz.services;

import com.codewithz.model.Order;
import com.codewithz.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderProducerService {

    @Value("${spring.kafka.topic.name:bot_order_data}")
    private String topicName;

    private final KafkaTemplate<String, Order> kafkaTemplateForOrder;

    @Autowired
    public KafkaOrderProducerService( @Qualifier("orderKafkaTemplate") KafkaTemplate<String, Order> kafkaTemplateForOrder) {
        this.kafkaTemplateForOrder = kafkaTemplateForOrder;
    }

    public void sendOrder(Order order) {
        kafkaTemplateForOrder.send(topicName, order);
    }


}
