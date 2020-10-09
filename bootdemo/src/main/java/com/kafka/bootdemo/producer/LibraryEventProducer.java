package com.kafka.bootdemo.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.bootdemo.domain.LibraryEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
@Log4j2
public class LibraryEventProducer {

    @AllArgsConstructor
    private class LibraryEventListenableFutureCallback implements ListenableFutureCallback<SendResult<Integer, String>> {
        private final Integer key;
        private final String value;

        @Override
        public void onFailure(Throwable ex) { handleFailure(key, value, ex); }
        @Override
        public void onSuccess(SendResult<Integer, String> result) { handleSuccess(key, value, result); }
    }

    private final String topic = "library-events";

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ListenableFuture<SendResult<Integer, String>> result = kafkaTemplate.sendDefault(key, value);
        result.addCallback(new LibraryEventListenableFutureCallback(key, value));
    }

    public void sendLibraryEventV2(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);
        ListenableFuture<SendResult<Integer, String>> sendResult = kafkaTemplate.send(producerRecord);
        sendResult.addCallback(new LibraryEventListenableFutureCallback(key, value));
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> headerList = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, headerList);
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error Sending message, Exception: {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error on failure");
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent successfully for key {} with value {}, in partition: {}",
                key, value, result.getRecordMetadata().partition());
    }
}
