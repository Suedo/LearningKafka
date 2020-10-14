package com.kafka.bootdemo.repository;

import com.kafka.bootdemo.domain.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventRepository extends JpaRepository<LibraryEvent, Integer> {
}
