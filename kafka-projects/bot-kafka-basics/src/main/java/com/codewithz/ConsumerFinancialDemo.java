package com.codewithz;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerFinancialDemo {
    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "localhost:9092";
        String topic = "bot_tx_values";
        String group_id="bot_financialteam";

        Logger logger = LoggerFactory.getLogger(ConsumerFinancialDemo.class);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,group_id);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                DoubleDeserializer.class.getName());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //earliest/latest/none

        KafkaConsumer<String, Double> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            ConsumerRecords<String, Double> records = consumer.poll(Duration.ofSeconds(1));

            for(ConsumerRecord<String,Double> record:records){
                logger.info("---------------------------------");
                logger.info("K:"+record.key() +" || V:"+record.value());
                logger.info("P:"+record.partition() +" || O:"+record.offset());
            }
        }
    }
}
