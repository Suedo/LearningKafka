#enter the docker shell for kafka broker
docker-compose exec broker bash

# then issue command for creating a topic
kafka-topics --create --topic library-events --bootstrap-server broker:9092 --replication-factor 3 --partitions 3


# have a console consumer up before you hit a producer endpoint. this will help us visually see the output in the consumer
kafka-console-consumer --topic library-events --bootstrap-server broker:9092