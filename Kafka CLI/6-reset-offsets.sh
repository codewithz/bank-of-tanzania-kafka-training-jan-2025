#------------------------------------------------
#---------- Resetting Offset 
#------------------------------------------------

kafka-consumer-groups --bootstrap-server localhost:9092 
--group my-first-app --reset-offsets 
--to-earliest --execute --topic bot_first_topic