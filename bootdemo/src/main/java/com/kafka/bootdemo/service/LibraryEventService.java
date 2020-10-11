package com.kafka.bootdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.bootdemo.domain.Book;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.repository.LibraryEventRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class); // deserialize
        log.info("LibraryEvent consumed: {}", libraryEvent);

        switch (libraryEvent.getType()){
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                // update
                break;
            default:
                log.info("Invalid Library Event");
        }
    }

    private LibraryEvent save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        LibraryEvent savedLibraryEvent = repository.save(libraryEvent);
        log.info("Successfully saved library event: {}", savedLibraryEvent);
        return savedLibraryEvent;
    }
}
