package com.kafka.bootdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    @NotBlank
    private String bookName;

    @NotBlank
    private String bookAuthor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "library_event_id")
    private LibraryEvent libraryEvent;

}
