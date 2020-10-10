package com.kafka.bootdemo.consumer;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component // in favor of LibraryEventsConsumerManualOffset
@Log4j2
public class LibraryEventsConsumer {

    @KafkaListener(topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }
}
