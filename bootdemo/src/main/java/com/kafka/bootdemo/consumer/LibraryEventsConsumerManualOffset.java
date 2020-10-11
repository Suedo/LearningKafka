package com.kafka.bootdemo.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.bootdemo.service.LibraryEventService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class LibraryEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer, String> {

    @Autowired
    private LibraryEventService libraryEventService;

    @KafkaListener(topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Consumer Record: {}", consumerRecord);
        try {
            libraryEventService.processLibraryEvent(consumerRecord);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        acknowledgment.acknowledge(); // manual ack
    }
}
