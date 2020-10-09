package com.kafka.bootdemo.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.domain.LibraryEventType;
import com.kafka.bootdemo.producer.LibraryEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/lib")
public class LibraryEventResource {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping(path = "/add")
    public ResponseEntity<LibraryEvent> addBookToLibrary(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        libraryEvent.setType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEventV2(libraryEvent);
        return new ResponseEntity(new LibraryEvent(), HttpStatus.CREATED);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<LibraryEvent> updateLibraryBook(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        if(libraryEvent.getLibraryEventId() == null) {
            return ResponseEntity.badRequest().body(libraryEvent);
        }
        libraryEvent.setType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEventV2(libraryEvent);
        return new ResponseEntity(new LibraryEvent(), HttpStatus.CREATED);
    }
}
