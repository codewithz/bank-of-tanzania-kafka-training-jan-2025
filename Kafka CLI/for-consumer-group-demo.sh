1. Create a topic with 3 partitions 

kafka-topics --bootstrap-server localhost:9092 --topic bot_first_topic --create --partitions 3 
--replication-factor 1
2. Start a kafka-console-producer and write the messages to the topic 
IN CMD -1

kafka-console-producer --bootstrap-server localhost:9092 
--topic bot_first_topic

3. Start a consumer group - my-first-group 
IN CMD 2
kafka-console-consumer --bootstrap-server localhost:9092 
--topic bot_second_topic --group my-first-app

IN CMD 3
kafka-console-consumer --bootstrap-server localhost:9092 
--topic bot_second_topic --group my-first-app


4. Monitor the description of reading

kafka-consumer-groups --bootstrap-server localhost:9092 
--describe --group my-first-app