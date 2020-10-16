package com.kafka.bootdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.repository.LibraryEventRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;


@Service
@Log4j2
@Transactional
public class LibraryEventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LibraryEventRepository repository;

    @Autowired
    BookService bookService;

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class); // deserialize
        log.info("LibraryEvent consumed: {}", libraryEvent);

        switch (libraryEvent.getType()) {
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                update(libraryEvent);
                break;
            default:
                log.info("Invalid Library Event");
        }
    }

    public List<LibraryEvent> findAll() {
        return repository.findAll();
    }

    private LibraryEvent save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        LibraryEvent savedLibraryEvent = repository.save(libraryEvent);
        log.info("Successfully saved library event: {}", savedLibraryEvent);
        return savedLibraryEvent;
    }

    private LibraryEvent update(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null) throw new IllegalArgumentException("Id not provided");
        if (libraryEvent.getLibraryEventId() == 0)
            throw new IllegalArgumentException("For testing specific retry policies");

        LibraryEvent updates = repository.findById(libraryEvent.getLibraryEventId()).map(l -> {
            l.getBook().setBookName(libraryEvent.getBook().getBookName());
            l.getBook().setBookAuthor(libraryEvent.getBook().getBookAuthor());
            return l;
        }).orElseThrow(() ->
                // check ConfigHelper.simpleRetryPolicy() for retry logic
                // new IllegalArgumentException("Cannot find library event for the given id: " + libraryEvent.getLibraryEventId()); // won't be retried
                new RecoverableDataAccessException("Cannot find library event for the given id: " + libraryEvent.getLibraryEventId()) // will get retried 3 times
        );

        LibraryEvent savedUpdates = repository.save(updates);
        log.info("Successfully saved updated library event: {}", savedUpdates);
        return savedUpdates;

    }

    public void handleRecovery(ConsumerRecord<Integer, String> record) {
        Integer key = record.key();
        String value = record.value();
        ListenableFuture<SendResult<Integer, String>> result = kafkaTemplate.sendDefault(key, value);// recovery handling approach 1, send to producing topic

        result.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error Sending message, Exception: {}", ex.getMessage());
                try {
                    throw ex;
                } catch (Throwable throwable) {
                    log.error("Error on failure");
                }
            }

            @Override
            public void onSuccess(SendResult<Integer, String> integerStringSendResult) {
                log.info("Message sent successfully for key {} with value {}", key, value);
            }
        });
    }
}
