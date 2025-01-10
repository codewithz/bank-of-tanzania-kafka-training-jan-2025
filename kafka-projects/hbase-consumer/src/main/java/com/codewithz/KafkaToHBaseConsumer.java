package com.codewithz;

import com.codewithz.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Collections;
import java.util.Properties;

public class KafkaToHBaseConsumer {

    private static final String TOPIC_NAME = "bot_tx_data";
    private static final String HBASE_TABLE_NAME = "transactions";

    public static void main(String[] args) {
        // Kafka consumer configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "transaction-consumer-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));

        // HBase connection
        try (Connection connection = ConnectionFactory.createConnection()) {
            Table table = connection.getTable(TableName.valueOf(HBASE_TABLE_NAME));

            ObjectMapper objectMapper = new ObjectMapper();

            while (true) {
                System.out.println("Received Data");
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        // Deserialize transaction object
                        Transaction transaction = objectMapper.readValue(record.value(), Transaction.class);

                        // Create row key (e.g., based on timestamp or transaction ID)
                        String rowKey = transaction.getTxId();

                        // Prepare data for insertion
                        Put put = new Put(Bytes.toBytes(rowKey));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("tx_id"), Bytes.toBytes(transaction.getTxId()));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("bank_name"), Bytes.toBytes(transaction.getBankName()));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("account_holder"), Bytes.toBytes(transaction.getAccountHolder()));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("amount"), Bytes.toBytes(String.valueOf(transaction.getAmount())));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("balance_amount"), Bytes.toBytes(String.valueOf(transaction.getBalanceAmount())));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("country"), Bytes.toBytes(transaction.getCountry()));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("state"), Bytes.toBytes(transaction.getState()));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("timestamp"), Bytes.toBytes(transaction.getTimestamp()));
                        put.addColumn(Bytes.toBytes("details"), Bytes.toBytes("tx_type_code"), Bytes.toBytes(transaction.getTxTypeCode()));

                        // Insert into HBase
                        table.put(put);
                        System.out.println("Inserted transaction into HBase: " + transaction.getTxId());
                    } catch (Exception e) {
                        System.err.println("Error processing record: " + record.value());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

