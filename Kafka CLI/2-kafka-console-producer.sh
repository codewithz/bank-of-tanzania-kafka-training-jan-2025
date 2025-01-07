#------------------------------------------------
#---------- To Produce Data to a Topic [Bootstrap Server]
#------------------------------------------------

kafka-console-producer --bootstrap-server host:port 
--topic name_of_topic

kafka-console-producer --bootstrap-server localhost:9092 
--topic bot_first_topic

#------------------------------------------------
#---------- To Produce Data to a Topic with properties
#------------------------------------------------

kafka-console-producer --bootstrap-server host:port 
--topic name_of_topic --producer-property key=value

kafka-console-producer --bootstrap-server localhost:9092 
--topic bot_first_topic --producer-property acks=all

#------------------------------------------------
#---------- To Produce Data to a Non-Existing Topic [Bootstrap Server]
#------------------------------------------------

kafka-console-producer --bootstrap-server host:port 
--topic name_of_topic_which_doesnt_exists

kafka-console-producer --bootstrap-server localhost:9092 
--topic bot_fourth_topic

#### ----------- ALWAYS CREATE A TOPIC BEFORE PRODUCING DATA TO THEM --------------------