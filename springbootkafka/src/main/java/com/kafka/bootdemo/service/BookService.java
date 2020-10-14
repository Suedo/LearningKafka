package com.kafka.bootdemo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.bootdemo.domain.Book;
import com.kafka.bootdemo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BookService {

    @Autowired
    BookRepository repository;

    public Book save(Book book){
        return repository.save(book);
    }
}
