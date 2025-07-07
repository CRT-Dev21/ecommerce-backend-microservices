echo "Creating Kafka Topics..."

TOPICS=("order-created-topic" "stock-reserved-topic" "order-confirmed-topic" "user-created-topic")

for topic in "${TOPICS[@]}"; do
  kafka-topics --bootstrap-server kafka:9092 \
    --create --if-not-exists \
    --replication-factor 1 \
    --partitions 1 \
    --topic "$topic"
done

echo "Kafka Topics created."