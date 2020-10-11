package com.kafka.bootdemo.dto;

import com.kafka.bootdemo.domain.Book;
import com.kafka.bootdemo.domain.LibraryEvent;
import com.kafka.bootdemo.domain.LibraryEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
public class LibraryEventDto implements Serializable {
    private Integer libraryEventId;

    @Enumerated(EnumType.STRING)
    private LibraryEventType type;

    @NotBlank
    private BookDto book;

    public LibraryEventDto(LibraryEvent that){
        this.libraryEventId = that.getLibraryEventId();
        this.type = that.getType();
        this.book = new BookDto(that.getBook());
    }
}
