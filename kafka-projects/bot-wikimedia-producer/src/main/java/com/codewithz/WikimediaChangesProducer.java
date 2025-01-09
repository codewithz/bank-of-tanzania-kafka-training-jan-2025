package com.codewithz;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WikimediaChangesProducer {
    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "localhost:9092";
        String topic = "bot_wikimedia_messages";

        Logger logger= LoggerFactory.getLogger(WikimediaChangesProducer.class);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        // For Safe Producer Config

        props.
                setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.
                setProperty(ProducerConfig.ACKS_CONFIG, "all");
        props.
                setProperty(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));

        // High Throughput Producer
        // (At expense of latency and CPU Usage)

        props
                .setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props
                .setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        props
                .setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));


        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String url="https://stream.wikimedia.org/v2/stream/recentchange";

        EventHandler eventHandler=new WikimediaChangesHandler(producer,topic);

        EventSource.Builder builder=new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource=builder.build();

        eventSource.start();
        // We produce the messages for 10 minutes and stop
        TimeUnit.MINUTES.sleep(10);
    }
}
