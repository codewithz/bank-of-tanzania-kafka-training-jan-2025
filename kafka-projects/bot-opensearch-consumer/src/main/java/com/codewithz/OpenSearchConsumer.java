package com.codewithz;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OpenSearchConsumer {

    public static RestHighLevelClient createRestHighLevelClient() {
        String connectionString = "http://localhost:9200";

        RestHighLevelClient restHighLevelClient;

        URI url=URI.create(connectionString);

        String userInfo=url.getUserInfo();

        if(userInfo==null) {
//            Rest CLient without Security
            restHighLevelClient=
                    new RestHighLevelClient(
                            RestClient.builder(
                                    new HttpHost(url.getHost(), url.getPort(), "http")
                            ));
        }
        else{
            restHighLevelClient=null;
        }
        return restHighLevelClient;
    }

    public static KafkaConsumer createKafkaConsumer(){
        String bootstrapServers="localhost:9093";
        String groupId="opensearch-consumer-group";

        Properties properties=new Properties();
        properties.
                setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers);
        properties.
                setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class.getName());
        properties.
                setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class.getName());
        properties.
                setProperty(ConsumerConfig.GROUP_ID_CONFIG,
                        groupId );
        properties.
                setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        "latest" );
        // earliest/latest/none
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        return consumer;

    }

    public static void main(String[] args) throws IOException {
        Logger logger= LoggerFactory.getLogger(OpenSearchConsumer.class);

//        Create a Open Search Client

        RestHighLevelClient openSearchClient=createRestHighLevelClient();

//        We need to create an index if it doesn't exist

        boolean indexExists=openSearchClient
                                .indices()
                                 .exists(
                                         new GetIndexRequest("bot_wikimedia_messages"),
                                         RequestOptions.DEFAULT);

        if(!indexExists){
            CreateIndexRequest createIndexRequest=
                    new CreateIndexRequest("bot_wikimedia_messages");
            openSearchClient
                    .indices()
                    .create(createIndexRequest,
                            RequestOptions.DEFAULT);

            logger.info("Index bot_wikimedia_messages has been creted");
        }else{
            logger.info("Index already exists");
        }

//        ----------------Creating Kafka Consumer -------------------

        KafkaConsumer<String, String> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singleton("bot_wikimedia_messages"));

        while(true){
            ConsumerRecords<String,String> records=consumer.poll(Duration.ofSeconds(3));

            int recordsCount=records.count();
            logger.info(recordsCount + " records found");

            for(ConsumerRecord<String,String> record:records){
                // Send Records to OpenSearch
                // TO make records unique for OpenSearch we will have to assign a key

                String id=record.topic()+"_"+record.partition()+"_"+record.offset();
                try {
                    IndexRequest indexRequest=new IndexRequest("bot_wikimedia_messages")
                            .source(record.value(),
                                    XContentType.JSON)
                            .id(id);

                    IndexResponse response=openSearchClient
                            .index(indexRequest, RequestOptions.DEFAULT);

                    logger.info("Inserted 1 document in OpenSearch with id  :"+response.getId());
                } catch (Exception e) {
                    logger.error("Exception",e);
                    // TODO: handle exception
                }

                consumer.commitSync();
                logger.info("Offset has been commited");

            }
        }
    }
}
