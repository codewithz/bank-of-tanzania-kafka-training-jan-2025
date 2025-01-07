#!/bin/bash

# Configuration: Update these variables on each machine
BROKER_ID=1                 # Set to 1 for kafka1, 2 for kafka2, 3 for kafka3
IP="192.168.100.1"         # Replace with the IP address of the current machine
HOSTS="192.168.100.1 kafka1\n192.168.100.2 kafka2\n192.168.100.3 kafka3"
ZOOKEEPER_SERVERS="192.168.100.1:2181,192.168.100.2:2181,192.168.100.3:2181"
KAFKA_VERSION="3.5.0"
KAFKA_DIR="/opt/kafka"

# Step 1: Install Java
sudo apt update
sudo apt install -y openjdk-11-jdk
java -version || { echo "Java installation failed!"; exit 1; }

# Step 2: Download and Extract Kafka
if [ ! -d "$KAFKA_DIR" ]; then
  wget "https://downloads.apache.org/kafka/$KAFKA_VERSION/kafka_2.13-$KAFKA_VERSION.tgz"
  tar -xvzf "kafka_2.13-$KAFKA_VERSION.tgz"
  sudo mv "kafka_2.13-$KAFKA_VERSION" "$KAFKA_DIR"
fi

# Step 3: Update /etc/hosts
sudo bash -c "echo -e '$HOSTS' >> /etc/hosts"

# Step 4: Configure Zookeeper
sudo mkdir -p /var/lib/zookeeper
sudo bash -c "echo '$BROKER_ID' > /var/lib/zookeeper/myid"
cat > "$KAFKA_DIR/config/zookeeper.properties" <<EOL
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
initLimit=5
syncLimit=2
server.1=192.168.100.1:2888:3888
server.2=192.168.100.2:2888:3888
server.3=192.168.100.3:2888:3888
EOL

# Step 5: Configure Kafka Broker
sudo mkdir -p /var/lib/kafka-logs
cat > "$KAFKA_DIR/config/server.properties" <<EOL
broker.id=$BROKER_ID
listeners=PLAINTEXT://$IP:9092
log.dirs=/var/lib/kafka-logs
zookeeper.connect=$ZOOKEEPER_SERVERS
num.network.threads=3
num.io.threads=8
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
EOL

# Step 6: Create Systemd Service Files
# Zookeeper Service
sudo bash -c 'cat > /etc/systemd/system/zookeeper.service' <<EOL
[Unit]
Description=Apache Zookeeper
After=network.target

[Service]
ExecStart=$KAFKA_DIR/bin/zookeeper-server-start.sh $KAFKA_DIR/config/zookeeper.properties
ExecStop=$KAFKA_DIR/bin/zookeeper-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target
EOL

# Kafka Service
sudo bash -c 'cat > /etc/systemd/system/kafka.service' <<EOL
[Unit]
Description=Apache Kafka
After=zookeeper.service

[Service]
ExecStart=$KAFKA_DIR/bin/kafka-server-start.sh $KAFKA_DIR/config/server.properties
ExecStop=$KAFKA_DIR/bin/kafka-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target
EOL

# Step 7: Enable and Start Services
sudo systemctl daemon-reload
sudo systemctl enable zookeeper
sudo systemctl enable kafka
sudo systemctl start zookeeper
sudo systemctl start kafka

# Step 8: Verify Setup
if sudo systemctl is-active --quiet kafka && sudo systemctl is-active --quiet zookeeper; then
  echo "Kafka and Zookeeper services are running!"
else
  echo "Failed to start Kafka or Zookeeper. Check logs."
fi

# Instructions for Creating a Topic and Testing
cat <<EOL
Setup Complete! To test your Kafka cluster:
1. Create a topic from any Kafka broker:
   $KAFKA_DIR/bin/kafka-topics.sh --create --topic test-topic --bootstrap-server $IP:9092 --replication-factor 3 --partitions 1

2. Produce messages:
   $KAFKA_DIR/bin/kafka-console-producer.sh --topic test-topic --bootstrap-server $IP:9092

3. Consume messages:
   $KAFKA_DIR/bin/kafka-console-consumer.sh --topic test-topic --from-beginning --bootstrap-server $IP:9092
EOL
