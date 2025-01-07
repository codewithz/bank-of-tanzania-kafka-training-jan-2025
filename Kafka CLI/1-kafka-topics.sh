#------------------------------------------------
#---------- To Create a Topic [Zookeper]
#------------------------------------------------

kafka-topics --zookeeper host:port --topic name_of_topic --create 
--partitions noOfPartitions --replication-factor noOfReplicas

#------------------------------------------------
#---------- To Create a Topic [Bootstrap Server]
#------------------------------------------------

kafka-topics --bootstrap-server host:port --topic name_of_topic --create 
--partitions noOfPartitions --replication-factor noOfReplicas

kafka-topics --bootstrap-server localhost:9092 --topic bot_first_topic --create --partitions 3 
--replication-factor 1

#------------------------------------------------
#---------- To List the Topics [Bootstrap Server]
#------------------------------------------------

kafka-topics --bootstrap-server host:port --List

kafka-topics -bootstrap-server localhost:9092 --list

#------------------------------------------------
#---------- To Describe a Topic [Bootstrap Server]
#------------------------------------------------

kafka-topics --bootstrap-server host:port --topic name_of_topic --describe 

kafka-topics --bootstrap-server localhost:9092 --topic bot_first_topic --describe 

Topic: bot_first_topic       TopicId: vvukflr0RoudVYxs0kl4LA PartitionCount: 3       ReplicationFactor: 1    Configs: segment.bytes=1073741824
        Topic: bot_first_topic       Partition: 0    Leader: 0       Replicas: 0     Isr: 0
        Topic: bot_first_topic       Partition: 1    Leader: 0       Replicas: 0     Isr: 0
        Topic: bot_first_topic       Partition: 2    Leader: 0       Replicas: 0     Isr: 0



#------------------------------------------------
#---------- Delete a Topic [Bootstrap Server]
#------------------------------------------------

kafka-topics --bootstrap-server host:port --topic name_of_topic --delete 

kafka-topics --bootstrap-server localhost:9092 --topic bot_first_topic --delete 


