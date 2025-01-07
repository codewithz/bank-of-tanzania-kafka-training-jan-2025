#------------------------------------------------
#---------- To Consume Data from a Topic 
#------------------------------------------------

kafka-console-consumer --bootstrap-server host:port 
--topic name_of_topic

kafka-console-consumer --bootstrap-server localhost:9092 
--topic bot_first_topic

#------------------------------------------------
#---------- To Consume Data from a Topic from beginning
#------------------------------------------------

kafka-console-consumer --bootstrap-server host:port 
--topic name_of_topic --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 
--topic bot_first_topic --from-beginning