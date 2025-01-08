package com.codewithz;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "bot_first_topic";

        Logger logger= LoggerFactory.getLogger(ProducerDemoWithCallback.class);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        for (int key=1;key<=100;key++){
            ProducerRecord<String,String> producerRecord=
                    new ProducerRecord<>(topic,String.valueOf(key),"BOT-"+key);

            producer.send(producerRecord, new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        logger.error("Error while producing the record");
                        logger.error(exception.getMessage());
                    }else {
                        logger.info("New Meta Data recieved");
                        logger.info("Topic:"+metadata.topic());
                        logger.info("Partition:"+metadata.partition());
                        logger.info("Offset:"+metadata.offset());
                        logger.info("Timestamp:"+metadata.timestamp());


                    }
                }
            });

            producer.flush();
        }
    }
}
