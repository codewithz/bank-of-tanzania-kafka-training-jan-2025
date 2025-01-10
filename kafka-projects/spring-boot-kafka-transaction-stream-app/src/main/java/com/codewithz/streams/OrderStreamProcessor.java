package com.codewithz.streams;

import com.codewithz.model.Order;
import com.codewithz.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class OrderStreamProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LinkedBlockingQueue<Order> processedQueue = new LinkedBlockingQueue<>();
    private final Map kafkaStreamsConfiguration;

    public OrderStreamProcessor(Map kafkaStreamsConfiguration) {
        this.kafkaStreamsConfiguration = kafkaStreamsConfiguration;
    }

    @PostConstruct
    public void processStream() {
        StreamsBuilder builder = new StreamsBuilder();

        // Process Order topic
        KStream<String, String> orderStream = builder.stream("bot_order_data",
                Consumed.with(Serdes.String(), Serdes.String()));
        orderStream
                .mapValues(this::validateAndTransformOrder) // Process Order
                .filter((key, value) -> value != null)
                .peek((key, value) -> {
                    try {
                        // Perform side effects here, such as placing data into a queue
                        processedQueue.put(objectMapper.readValue(value, Order.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .to("bot_processed_order", Produced.with(Serdes.String(), Serdes.String())); // Send to Kafka topic


        // Start the Kafka Streams application
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), new StreamsConfig(kafkaStreamsConfiguration));
        kafkaStreams.start();
    }

    private String validateAndTransformOrder(String orderJson) {
        try {
            // Deserialize the JSON into an Order object
            Order order = objectMapper.readValue(orderJson, Order.class);

            // Validation
            if (order.getQuantity() <= 0) {
                return null; // Drop invalid orders
            }

            // Transformation
            order.setStatus("Processed");

            // Serialize back to JSON
            return objectMapper.writeValueAsString(order);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Drop invalid JSON
        }
    }


    public Order getValidatedOrder() {
        try {
            return processedQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Order", e);
        }
    }
    

}
