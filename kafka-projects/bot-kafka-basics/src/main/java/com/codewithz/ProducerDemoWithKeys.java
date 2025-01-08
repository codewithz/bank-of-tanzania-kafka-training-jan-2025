package com.codewithz;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {

    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "localhost:9092";
        String topic = "bot_tx_values";

        Logger logger= LoggerFactory.getLogger(ProducerDemoWithKeys.class);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                DoubleSerializer.class.getName());

        KafkaProducer<String, Double> producer = new KafkaProducer<>(props);

        String regionString =
                "Arusha,Dar es Salaam,Dodoma,Geita,Iringa,Kagera,Katavi,Kigoma,Kilimanjaro,Lindi," +
                        "Manyara,Mara,Mbeya,Morogoro,Mtwara,Mwanza,Njombe,Pemba North,Pemba South," +
                        "Pwani,Rukwa,Ruvuma,Shinyanga,Simiyu,Songwe,Tabora,Tanga,Zanzibar Central," +
                        "Zanzibar North,Zanzibar South";
        String[] regionArray = regionString.split(",");

        for (int i = 0; i < 25000; i++) {
            // Generate random region as the key
            String key = regionArray[(int) Math.floor(Math.random() * regionArray.length)];

            // Generate random value for the order amount
            double value = Math.floor(Math.random() * (10000 - 10 + 1) + 10);

            // Create producer record
            ProducerRecord<String, Double> producerRecord =
                    new ProducerRecord<>(topic, key, value);

            logger.info("Sending message with key " + key + " to Kafka");

            producer.send(producerRecord, (metadata, e) -> {
                if (metadata != null) {
                    logger.info("-----------------------------------");
                    logger.info("Key: " + producerRecord.key());
                    logger.info("Value: " + producerRecord.value());
                    logger.info("Metadata: " + metadata.toString());
                    logger.info("Topic:"+metadata.topic());
                    logger.info("Partition:"+metadata.partition());
                    logger.info("Offset:"+metadata.offset());
                    logger.info("Timestamp:"+metadata.timestamp());
                } else if (e != null) {
                    logger.error("Error sending message to Kafka", e);
                }
            });

            // Sleep for 1 second between messages
            Thread.sleep(1000);
        }


        producer.flush();
        }
    }

