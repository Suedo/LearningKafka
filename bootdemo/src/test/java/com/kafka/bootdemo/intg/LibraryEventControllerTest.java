package com.kafka.bootdemo.intg;

import com.kafka.bootdemo.domain.Book;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.domain.LibraryEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryEventControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    // we do not give url = "http://localhost:8899/v1/lib", as the test is going to start in a random port each time
    private final String url = "/v1/lib";

    @Test
    void addBook() {
        // given
        Book book = Book.builder().bookId(1).bookAuthor("Somjit").bookName("ABCD").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().type(LibraryEventType.NEW).libraryEventId(null).book(book).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<? extends LibraryEvent> responseEntity =
                restTemplate.exchange(url + "/add", HttpMethod.POST, request, LibraryEvent.class);

        // then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}
