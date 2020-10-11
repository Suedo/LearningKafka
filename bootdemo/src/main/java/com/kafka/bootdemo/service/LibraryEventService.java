package com.kafka.bootdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.repository.LibraryEventRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        LibraryEvent updates = repository.findById(libraryEvent.getLibraryEventId()).map(l -> {
            l.getBook().setBookName(libraryEvent.getBook().getBookName());
            l.getBook().setBookAuthor(libraryEvent.getBook().getBookAuthor());
            return l;
        }).orElseThrow(() -> new IllegalArgumentException("Cannot find library event for the given id: " + libraryEvent.getLibraryEventId()));

        LibraryEvent savedUpdates = repository.save(updates);
        log.info("Successfully saved updated library event: {}", savedUpdates);
        return savedUpdates;

    }
}
