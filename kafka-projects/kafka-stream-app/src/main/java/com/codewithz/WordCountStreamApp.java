package com.codewithz;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

public class WordCountStreamApp {

    public static void main(String[] args) {
        final String BOOTSTRAP_SERVERS = "localhost:9092";
        final String INPUT_TOPIC = "bot_wordcount_input";
        final String OUTPUT_TOPIC = "bot_wordcount_output";

        Properties props = new Properties();
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String,String> wordCountInput = builder.stream(INPUT_TOPIC);


        KTable<String, Long> wordCountOutput = wordCountInput
//                            Map the text to lower case
                .mapValues(textLine -> textLine.toLowerCase())
//                Split by Space
                .flatMapValues(lowerCasedTextLine-> Arrays.asList(lowerCasedTextLine.split(" ")))
//                Select Key --> to apply a key to a recoed
                .selectKey((nullKey,word)->word)
//                Group By Key
                .groupByKey()
//                Count the occurrence
                .count();

        wordCountOutput.toStream().to(OUTPUT_TOPIC, Produced.with(Serdes.String(),Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.start();

//        Print the Topology
        System.out.println(streams.toString());

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
