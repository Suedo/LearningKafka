package com.kafka.bootdemo.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.domain.LibraryEventType;
import com.kafka.bootdemo.dto.LibraryEventDto;
import com.kafka.bootdemo.producer.LibraryEventProducer;
import com.kafka.bootdemo.service.LibraryEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/lib")
public class LibraryEventResource {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @Autowired
    LibraryEventService libraryEventService;

    @PostMapping(path = "/add")
    public ResponseEntity<LibraryEvent> addBookToLibrary(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        libraryEvent.setType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEventV2(libraryEvent);
        return new ResponseEntity("Library event produced", HttpStatus.CREATED);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<LibraryEvent> updateLibraryBook(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        if (libraryEvent.getLibraryEventId() == null) {
            return ResponseEntity.badRequest().body(libraryEvent);
        }
        libraryEvent.setType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEventV2(libraryEvent);
        return new ResponseEntity("Library event updated", HttpStatus.CREATED);
    }


    @GetMapping(path = "/all")
    public ResponseEntity<List<LibraryEventDto>> getAllLibraryEvents() {
        List<LibraryEventDto> all =
                libraryEventService.findAll().stream().map(LibraryEventDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }
}
