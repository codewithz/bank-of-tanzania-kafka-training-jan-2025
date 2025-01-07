kafka-consumer-groups --bootstrap-server localhost:9092 --list


kafka-consumer-groups --bootstrap-server localhost:9092 
--describe --group my-first-app

GROUP           TOPIC               PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
my-first-app    bot_second_topic 0          8               8               0               -               -               -
my-first-app    bot_second_topic 1          8               8               0               -               -               -
my-first-app    bot_second_topic 2          8               8               0               -               -               -
