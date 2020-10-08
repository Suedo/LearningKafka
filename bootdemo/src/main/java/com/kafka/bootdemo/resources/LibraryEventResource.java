package com.kafka.bootdemo.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.producer.LibraryEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/lib")
public class LibraryEventResource {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping(path = "/event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        libraryEventProducer.sendLibraryEvent(libraryEvent);
        return new ResponseEntity(new LibraryEvent(), HttpStatus.CREATED);
    }
}
