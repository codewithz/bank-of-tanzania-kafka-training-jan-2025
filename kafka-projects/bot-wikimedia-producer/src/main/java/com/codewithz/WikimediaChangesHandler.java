package com.codewithz;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikimediaChangesHandler implements EventHandler {

    Logger logger= LoggerFactory.getLogger(WikimediaChangesHandler.class);
    KafkaProducer<String,String> kafkaProducer;
    String topic;

    public WikimediaChangesHandler(KafkaProducer<String,String> producer,String topic) {
        this.kafkaProducer = producer;
        this.topic = topic;
    }

    @Override
    public void onOpen() throws Exception {
        // DO NOTHING
    }

    @Override
    public void onClosed() throws Exception {
        kafkaProducer.flush();
        kafkaProducer.close();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
//        Send the data to the producer
        ProducerRecord<String,String> record = new ProducerRecord<>(topic,messageEvent.getData());
        kafkaProducer.send(record, (metadata, e) -> {
            if (metadata != null) {
                logger.info("-----------------------------------");
                logger.info("Key: " + record.key());
                logger.info("Value: " + record.value());
                logger.info("Metadata: " + metadata.toString());
                logger.info("Topic:"+metadata.topic());
                logger.info("Partition:"+metadata.partition());
                logger.info("Offset:"+metadata.offset());
                logger.info("Timestamp:"+metadata.timestamp());
            } else if (e != null) {
                logger.error("Error sending message to Kafka", e);
            }
        });

        Thread.sleep(1000);

    }

    @Override
    public void onComment(String comment) throws Exception {

    }

    @Override
    public void onError(Throwable t) {
    logger.error("Error in WikimediaChangesHandler", t);
    }
}
