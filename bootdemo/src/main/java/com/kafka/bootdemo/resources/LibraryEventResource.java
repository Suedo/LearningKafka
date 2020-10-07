package com.kafka.bootdemo.resources;

import com.kafka.bootdemo.domain.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("/v1/lib")
public class LibraryEventResource {

    @PostMapping("/event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@Valid @RequestBody LibraryEvent libraryEvent){

        return new ResponseEntity(new LibraryEvent(), HttpStatus.CREATED);
    }
}
