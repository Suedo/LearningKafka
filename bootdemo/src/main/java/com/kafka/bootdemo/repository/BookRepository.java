package com.kafka.bootdemo.repository;

import com.kafka.bootdemo.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
